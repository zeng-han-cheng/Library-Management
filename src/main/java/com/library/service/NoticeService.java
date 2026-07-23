package com.library.service; import com.library.common.model.PageResult; import com.library.model.entity.Notice; import java.util.List;
public interface NoticeService { List<Notice> publicList(); PageResult<Notice> page(int page,int size); void save(Notice item); void update(Notice item); void delete(Long id); }
