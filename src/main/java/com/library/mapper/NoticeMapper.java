package com.library.mapper; import com.library.model.entity.Notice; import java.util.List; import org.apache.ibatis.annotations.Param;
public interface NoticeMapper { List<Notice> publicList(); List<Notice> page(@Param("offset") int offset,@Param("limit") int limit); long count(); int insert(Notice item); int update(Notice item); int delete(Long id); }
