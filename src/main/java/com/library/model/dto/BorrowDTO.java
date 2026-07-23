package com.library.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** One request may create multiple single-book borrowing records. */
public record BorrowDTO(Long readerId,
                        @NotNull Long bookId,
                        @Min(1) @Max(90) Integer days,
                        @Min(1) @Max(20) Integer quantity,
                        String remark) {
}
