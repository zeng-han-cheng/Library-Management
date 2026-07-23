package com.library.server.service; import com.library.common.model.PageResult; import java.util.List;
public interface CrudService<T> { PageResult<T> page(String keyword,int page,int size); T get(Long id); void save(T item); void update(T item); void delete(Long id); }
