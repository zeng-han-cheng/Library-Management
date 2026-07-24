package com.library.mapper;

import com.library.model.entity.Reader;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

public interface ReaderMapper {
    Reader findByUsername(String username);
    Reader findById(Long id);
    List<Reader> page(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);
    long count(String keyword);
    int insert(Reader item);
    int update(Reader item);
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    int delete(Long id);
    int countBorrowing(Long readerId);
    Map<String, Object> operationStats();
}
