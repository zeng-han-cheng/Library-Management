package com.library.model.dto;
public record BookQuery(Integer page,Integer size,String keyword,Long categoryId,Integer status) { public int offset(){return (page==null?1:page-1)*(size==null?10:size);} public int limit(){return size==null?10:size;} }
