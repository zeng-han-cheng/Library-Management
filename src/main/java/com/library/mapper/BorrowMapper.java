package com.library.mapper;

import com.library.model.entity.BorrowRecord;
import com.library.model.vo.BorrowVO;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface BorrowMapper {
    BorrowRecord findById(Long id);
    List<BorrowVO> page(@Param("readerId") Long readerId, @Param("offset") int offset, @Param("limit") int limit);
    long count(@Param("readerId") Long readerId);
    int insert(BorrowRecord item);
    int returnBook(BorrowRecord item);
    List<BorrowVO> timeline(Long readerId);
    Map<String, Object> operationStats();
    List<Map<String, Object>> popularBooks();
}
