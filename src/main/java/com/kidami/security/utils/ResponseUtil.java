package com.kidami.security.utils;

import com.kidami.security.responses.ApiResponse;

public class ResponseUtil {

    public static <T> ApiResponse<T> success(String message, T data, Object metaData) {
        return new ApiResponse<>("success", message, data, metaData);
    }

    public static <T> ApiResponse<T> error(String message, T data, Object metaData) {
        return new ApiResponse<>("error", message, data, metaData);
    }

    public static <T> ApiResponse<T> created(String message, T data, Object metaData) {
        return new ApiResponse<>("created", message, data, metaData);
    }

    public static <T> ApiResponse<T> noContent(String message) {
        return new ApiResponse<>("success", message, null, null);
    }
}