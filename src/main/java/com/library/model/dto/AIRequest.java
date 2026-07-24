package com.library.model.dto;

import jakarta.validation.constraints.Size;

public class AIRequest {
    @Size(max = 1000, message = "补充说明不能超过1000字")
    private String prompt;

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
