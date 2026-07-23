package com.library.service.impl;

import com.library.common.exception.BusinessException;
import com.library.common.model.PageResult;
import com.library.mapper.NoticeMapper;
import com.library.model.entity.Notice;
import com.library.service.NoticeService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeServiceImpl implements NoticeService {
    private final NoticeMapper mapper;

    public NoticeServiceImpl(NoticeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Notice> publicList() {
        return mapper.publicList();
    }

    @Override
    public PageResult<Notice> page(int page, int size) {
        return new PageResult<>(mapper.count(), page, size, mapper.page((page - 1) * size, size));
    }

    @Override
    public void save(Notice notice) {
        if (notice == null || notice.getTitle() == null || notice.getTitle().isBlank()
                || notice.getContent() == null || notice.getContent().isBlank()
                || notice.getAdminId() == null) {
            throw new BusinessException("公告标题、内容和发布人不能为空");
        }
        if (Integer.valueOf(1).equals(notice.getPublishStatus())) {
            notice.setPublishedAt(LocalDateTime.now());
        }
        if (mapper.insert(notice) != 1) {
            throw new BusinessException("公告发布失败");
        }
    }

    @Override
    public void update(Notice notice) {
        if (notice == null || notice.getId() == null || mapper.update(notice) != 1) {
            throw new BusinessException("公告不存在或更新失败");
        }
    }

    @Override
    public void delete(Long id) {
        if (mapper.delete(id) != 1) {
            throw new BusinessException("公告不存在或删除失败");
        }
    }
}
