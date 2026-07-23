package com.library.pojo.entity;

import java.time.LocalDateTime;

public class BaseEntity { protected Long id; protected LocalDateTime createdAt; protected LocalDateTime updatedAt;
 public Long getId(){return id;} public void setId(Long v){id=v;} public LocalDateTime getCreatedAt(){return createdAt;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public LocalDateTime getUpdatedAt(){return updatedAt;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;} }
