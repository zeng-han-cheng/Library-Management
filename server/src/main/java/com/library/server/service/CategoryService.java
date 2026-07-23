package com.library.server.service; import com.library.pojo.entity.Category; import java.util.List;
public interface CategoryService { List<Category> list(); void save(Category item); void update(Category item); void delete(Long id); }
