package com.library.service;

import com.library.model.dto.AIRequest;

public interface AIService {
    String readerRecommend(AIRequest request);
    String adminAdvice(AIRequest request);
}
