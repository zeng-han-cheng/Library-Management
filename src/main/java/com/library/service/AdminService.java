package com.library.service; import com.library.common.model.PageResult; import com.library.model.entity.Admin;
public interface AdminService { PageResult<Admin> page(String keyword,int page,int size); void save(Admin item); void update(Admin item); void delete(Long id); }
