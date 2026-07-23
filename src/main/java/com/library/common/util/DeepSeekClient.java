package com.library.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** DeepSeek统一客户端：超时、异常和空响应均转换为可控业务错误。 */
@Component
public class DeepSeekClient {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);
    private final ObjectMapper mapper = new ObjectMapper(); private final HttpClient client;
    private final String apiKey; private final String endpoint; private final String model;
    public DeepSeekClient(@Value("${deepseek.api-key:}") String apiKey, @Value("${deepseek.endpoint:https://api.deepseek.com/chat/completions}") String endpoint, @Value("${deepseek.model:deepseek-chat}") String model, @Value("${deepseek.timeout-ms:10000}") long timeout) {
        this.apiKey=apiKey; this.endpoint=endpoint; this.model=model; this.client=HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeout)).build();
    }
    public String chat(String prompt) {
        if (apiKey == null || apiKey.isBlank()) return "AI服务未配置，请设置 DEEPSEEK_API_KEY 环境变量。";
        try { String body=mapper.writeValueAsString(Map.of("model",model,"messages",List.of(Map.of("role","user","content",prompt)),"stream",false));
            HttpRequest request=HttpRequest.newBuilder(URI.create(endpoint)).timeout(Duration.ofSeconds(10)).header("Authorization","Bearer "+apiKey).header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(body)).build();
            JsonNode root=mapper.readTree(client.send(request,HttpResponse.BodyHandlers.ofString()).body()); JsonNode content=root.path("choices").path(0).path("message").path("content");
            if (content.isMissingNode()) return "AI暂时没有返回有效结果。"; return content.asText();
        } catch (Exception e) { log.warn("DeepSeek request failed",e); return "AI服务暂时不可用，请稍后重试。"; }
    }
}
