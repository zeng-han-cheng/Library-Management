package com.library.pojo.dto;
import jakarta.validation.constraints.NotNull;
public record ReturnDTO(@NotNull Long recordId,@NotNull Integer status,String remark) { }
