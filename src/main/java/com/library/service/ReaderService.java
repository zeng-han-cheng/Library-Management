package com.library.service; import com.library.common.model.PageResult; import com.library.model.entity.Reader;
public interface ReaderService { PageResult<Reader> page(String keyword,int page,int size); Reader get(Long id); void save(Reader item); void update(Reader item); void delete(Long id); }
