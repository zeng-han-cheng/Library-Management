package com.library.model.dto;
import jakarta.validation.constraints.NotNull;
public record ReturnDTO(@NotNull Long recordId,@NotNull Integer status,String remark) { }
