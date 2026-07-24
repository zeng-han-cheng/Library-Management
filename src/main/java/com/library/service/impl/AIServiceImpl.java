package com.library.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.common.util.DeepSeekClient;
import com.library.mapper.BookMapper;
import com.library.mapper.BorrowMapper;
import com.library.mapper.ReaderMapper;
import com.library.model.dto.AIOperationSnapshot;
import com.library.model.dto.AIRequest;
import com.library.service.AIService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AIServiceImpl implements AIService {
    private static final String OPERATION_SYSTEM = """
            你是图书馆运营分析顾问。请只基于用户提供的数据和问题回答，不要编造不存在的数字。
            输出简洁、可执行的中文运营建议，使用以下结构：
            一、关键发现（2-4条）；二、优先行动（按优先级列出3-5条，每条包含负责人建议和衡量指标）；三、风险与复盘（1-3条）。
            如果数据不足，请明确指出需要补充的数据。不要输出 JSON，不要使用夸张或空泛的营销话术。
            """;
    private static final String READER_SYSTEM = """
            你是图书馆借阅推荐助手。根据读者的兴趣生成具体、友好的中文推荐。
            如果没有书目清单，只能推荐阅读方向和筛选方法，不要虚构书名、库存或作者。
            先给出推荐方向，再说明选择理由和下一步借阅建议，控制在600字以内。
            """;

    private final DeepSeekClient client;
    private final BookMapper bookMapper;
    private final BorrowMapper borrowMapper;
    private final ReaderMapper readerMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AIServiceImpl(DeepSeekClient client, BookMapper bookMapper, BorrowMapper borrowMapper, ReaderMapper readerMapper) {
        this.client = client;
        this.bookMapper = bookMapper;
        this.borrowMapper = borrowMapper;
        this.readerMapper = readerMapper;
    }

    @Override
    public String readerRecommend(AIRequest request) {
        String prompt = safePrompt(request);
        String answer = client.chat(READER_SYSTEM, "读者需求：\n" + prompt);
        return answer != null ? answer : "AI 服务暂时不可用。你可以先按兴趣分类浏览图书，并优先选择有可借库存、简介完整的图书。";
    }

    @Override
    public String adminAdvice(AIRequest request) {
        AIOperationSnapshot snapshot = new AIOperationSnapshot(
                bookMapper.overviewStats(),
                borrowMapper.operationStats(),
                readerMapper.operationStats(),
                nullSafe(bookMapper.categoryStats()),
                nullSafe(borrowMapper.popularBooks()));
        String prompt = "请分析以下图书馆实时运营数据，并结合管理员补充问题给出建议。\n实时数据：\n"
                + toJson(snapshot) + "\n管理员补充问题：\n" + safePrompt(request);
        String answer = client.chat(OPERATION_SYSTEM, prompt);
        return answer != null ? answer : fallback(snapshot);
    }

    private String safePrompt(AIRequest request) {
        if (request == null || request.getPrompt() == null || request.getPrompt().isBlank()) {
            return "请根据当前数据给出一份本周运营建议。";
        }
        return request.getPrompt().trim();
    }

    private String toJson(AIOperationSnapshot snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException e) {
            return "运营数据序列化失败，请根据页面统计数据进行人工复核。";
        }
    }

    private <T> java.util.List<T> nullSafe(java.util.List<T> value) {
        return value == null ? Collections.emptyList() : value;
    }

    private String fallback(AIOperationSnapshot snapshot) {
        Object overdue = snapshot.borrowing() == null ? 0 : snapshot.borrowing().getOrDefault("overdueCount", 0);
        Object available = snapshot.overview() == null ? 0 : snapshot.overview().getOrDefault("availableStock", 0);
        return "当前无法连接 DeepSeek，先提供一份数据驱动的离线建议：\n\n"
                + "一、关键发现\n- 当前可借库存为 " + available + "，逾期记录为 " + overdue + "。\n"
                + "二、优先行动\n1. 优先处理逾期记录，建立到期前提醒和逾期跟进清单。\n"
                + "2. 对热门图书检查可借库存，结合分类库存决定补充采购或馆际调拨。\n"
                + "3. 每周复盘分类借阅量、库存周转和读者活跃数，持续调整采购计划。\n"
                + "三、风险与复盘\n- 离线建议未使用实时模型判断，请在 DeepSeek 恢复后重新生成并核对具体行动指标。";
    }
}
