package com.library.service.impl;

import com.library.common.exception.BusinessException;
import com.library.mapper.CategoryMapper;
import com.library.model.entity.Category;
import com.library.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper mapper;

    public CategoryServiceImpl(CategoryMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Category> list() {
        return mapper.list();
    }

    @Override
    public void save(Category category) {
        if (category == null || category.getName() == null || category.getName().isBlank()) {
            throw new BusinessException("分类名称不能为空");
        }
        if (mapper.insert(category) != 1) {
            throw new BusinessException("新增分类失败");
        }
    }

    @Override
    public void update(Category category) {
        if (category == null || category.getId() == null) {
            throw new BusinessException("分类编号不能为空");
        }
        if (mapper.update(category) != 1) {
            throw new BusinessException("分类不存在或更新失败");
        }
    }

    @Override
    public void delete(Long id) {
        if (mapper.delete(id) != 1) {
            throw new BusinessException("分类不存在或删除失败");
        }
    }
}
