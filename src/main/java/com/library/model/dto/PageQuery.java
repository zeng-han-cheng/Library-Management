package com.library.model.dto;
import jakarta.validation.constraints.Max; import jakarta.validation.constraints.Min;
public record PageQuery(@Min(1) Integer page,@Min(1) @Max(100) Integer size,String keyword) { public int offset(){return (page==null?1:page-1)*(size==null?10:size);} public int limit(){return size==null?10:size;} }
