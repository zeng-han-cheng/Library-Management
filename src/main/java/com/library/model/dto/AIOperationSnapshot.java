package com.library.model.dto;

import java.util.List;
import java.util.Map;

public record AIOperationSnapshot(
        Map<String, Object> overview,
        Map<String, Object> borrowing,
        Map<String, Object> readers,
        List<Map<String, Object>> categories,
        List<Map<String, Object>> popularBooks) { }
