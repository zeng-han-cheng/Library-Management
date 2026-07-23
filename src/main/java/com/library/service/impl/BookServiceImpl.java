package com.library.service.impl;

import com.library.common.exception.BusinessException;
import com.library.common.model.PageResult;
import com.library.mapper.BookMapper;
import com.library.model.dto.BookQuery;
import com.library.model.entity.Book;
import com.library.model.vo.BookVO;
import com.library.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {
    private final BookMapper mapper;

    public BookServiceImpl(BookMapper mapper) { this.mapper = mapper; }

    @Override public PageResult<BookVO> page(BookQuery query) {
        BookQuery safeQuery = query == null ? new BookQuery(1, 10, null, null, null) : query;
        int page = safeQuery.page() == null ? 1 : safeQuery.page(); int size = safeQuery.size() == null ? 10 : safeQuery.size();
        return new PageResult<>(mapper.count(safeQuery.keyword(), safeQuery.categoryId(), safeQuery.status()), page, size, mapper.page(safeQuery.keyword(), safeQuery.categoryId(), safeQuery.status(), safeQuery.offset(), safeQuery.limit()));
    }
    @Override public Book get(Long id) { return mapper.findById(id); }
    @Override public void save(Book book) {
        int totalStock = book.getTotalStock() == null ? 0 : book.getTotalStock();
        book.setTotalStock(totalStock); book.setAvailableStock(totalStock); book.setStatus(totalStock > 0 ? 1 : 2);
        if (mapper.insert(book) != 1) throw new BusinessException("新增图书失败");
    }
    @Override public void update(Book book) {
        Book oldBook = mapper.findById(book.getId()); if (oldBook == null) throw new BusinessException("图书不存在");
        int borrowedCount = oldBook.getTotalStock() - oldBook.getAvailableStock();
        int totalStock = book.getTotalStock() == null ? oldBook.getTotalStock() : book.getTotalStock();
        if (totalStock < borrowedCount) throw new BusinessException("总库存不能小于已借数量");
        book.setTotalStock(totalStock); book.setAvailableStock(totalStock - borrowedCount); book.setStatus(book.getAvailableStock() > 0 ? 1 : 2);
        if (mapper.update(book) != 1) throw new BusinessException("更新图书失败");
    }
    @Override public void delete(Long id) { if (mapper.delete(id) != 1) throw new BusinessException("图书不存在或删除失败"); }
    @Override public List<Map<String, Object>> stats() { return mapper.categoryStats(); }
    @Override public Map<String, Object> overview() { return mapper.overviewStats(); }
    @Override public Map<String, Object> availableOverview() { return mapper.availableOverview(); }
    @Override public void repairInventory() { mapper.repairInventory(); }
}
