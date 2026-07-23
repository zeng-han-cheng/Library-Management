package com.library.mapper; import com.library.model.entity.Category; import java.util.List;
public interface CategoryMapper { List<Category> list(); Category findById(Long id); int insert(Category item); int update(Category item); int delete(Long id); }
