package com.library.controller;

import com.library.common.annotation.RequireRole;
import com.library.common.constant.RoleConstant;
import com.library.common.model.ApiResult;
import com.library.model.dto.AIRequest;
import com.library.service.AIService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AIController {
    private final AIService service;

    public AIController(AIService service) { this.service = service; }

    @PostMapping("/recommend")
    @RequireRole(RoleConstant.READER)
    public ApiResult<String> recommend(@Valid @RequestBody AIRequest request) {
        return ApiResult.success(service.readerRecommend(request));
    }

    @PostMapping("/advice")
    @RequireRole(RoleConstant.ADMIN)
    public ApiResult<String> advice(@Valid @RequestBody AIRequest request) {
        return ApiResult.success(service.adminAdvice(request));
    }
}
