package com.library.model.dto;
import jakarta.validation.constraints.NotNull;
public record BorrowDTO(@NotNull Long readerId,@NotNull Long bookId,Integer days,String remark) { }
