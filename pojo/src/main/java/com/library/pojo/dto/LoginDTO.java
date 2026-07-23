package com.library.pojo.dto;
import jakarta.validation.constraints.NotBlank;
public record LoginDTO(@NotBlank String username,@NotBlank String password,@NotBlank String role) { }
