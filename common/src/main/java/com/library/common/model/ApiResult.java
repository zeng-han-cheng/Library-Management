package com.library.common.model;

import com.library.common.constant.StatusCode;

public class ApiResult<T> {
    private int code;
    private String message;
    private T data;
    public ApiResult() { }
    public ApiResult(int code, String message, T data) { this.code = code; this.message = message; this.data = data; }
    public static <T> ApiResult<T> success(T data) { return new ApiResult<>(StatusCode.SUCCESS, "success", data); }
    public static <T> ApiResult<T> success() { return success(null); }
    public static <T> ApiResult<T> error(int code, String message) { return new ApiResult<>(code, message, null); }
    public int getCode() { return code; } public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; } public void setMessage(String message) { this.message = message; }
    public T getData() { return data; } public void setData(T data) { this.data = data; }
}
