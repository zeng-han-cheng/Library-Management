package com.library.server.service; import com.library.common.model.PageResult; import com.library.pojo.entity.Notice; import java.util.List;
public interface NoticeService { List<Notice> publicList(); PageResult<Notice> page(int page,int size); void save(Notice item); void update(Notice item); void delete(Long id); }
