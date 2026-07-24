package com.library.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/** Calls DeepSeek's OpenAI-compatible chat completions endpoint. */
@Component
public class DeepSeekClient {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client;
    private final String apiKey;
    private final String endpoint;
    private final String model;
    private final long timeoutMs;

    public DeepSeekClient(
            @Value("${deepseek.api-key:}") String apiKey,
            @Value("${deepseek.endpoint:https://api.deepseek.com/chat/completions}") String endpoint,
            @Value("${deepseek.model:deepseek-v4-flash}") String model,
            @Value("${deepseek.timeout-ms:30000}") long timeoutMs) {
        this.apiKey = apiKey;
        this.endpoint = endpoint.endsWith("/chat/completions")
                ? endpoint
                : endpoint.replaceAll("/$", "") + "/chat/completions";
        this.model = model;
        this.timeoutMs = Math.max(1000, timeoutMs);
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(this.timeoutMs))
                .build();
    }

    public String chat(String prompt) {
        return chat("你是一个严谨、务实的图书馆智能助手。", prompt);
    }

    public String chat(String systemPrompt, String prompt) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("DeepSeek API key is not configured");
            return null;
        }
        try {
            String body = mapper.writeValueAsString(Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", prompt)),
                    "temperature", 0.3,
                    "stream", false));
            HttpRequest request = HttpRequest.newBuilder(URI.create(endpoint))
                    .timeout(Duration.ofMillis(timeoutMs))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                log.warn("DeepSeek request returned HTTP {}: {}", response.statusCode(), errorMessage(response.body()));
                return null;
            }
            JsonNode content = mapper.readTree(response.body())
                    .path("choices").path(0).path("message").path("content");
            if (content.isMissingNode() || content.asText().isBlank()) {
                throw new IllegalStateException("DeepSeek response did not contain message content");
            }
            return content.asText();
        } catch (Exception e) {
            log.warn("DeepSeek request failed: {}", e.getMessage());
            return null;
        }
    }

    private String errorMessage(String body) {
        try {
            JsonNode message = mapper.readTree(body).path("error").path("message");
            return message.isMissingNode() ? "unrecognized error response" : message.asText();
        } catch (Exception ignored) {
            return "unrecognized error response";
        }
    }
}
