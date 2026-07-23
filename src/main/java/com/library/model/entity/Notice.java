package com.library.model.entity;
import java.time.LocalDateTime;
public class Notice extends BaseEntity { private String title,content; private Long adminId; private Integer publishStatus; private LocalDateTime publishedAt;
 public String getTitle(){return title;} public void setTitle(String v){title=v;} public String getContent(){return content;} public void setContent(String v){content=v;} public Long getAdminId(){return adminId;} public void setAdminId(Long v){adminId=v;} public Integer getPublishStatus(){return publishStatus;} public void setPublishStatus(Integer v){publishStatus=v;} public LocalDateTime getPublishedAt(){return publishedAt;} public void setPublishedAt(LocalDateTime v){publishedAt=v;} }
