package io.github.tharundadesilva.traffic_report_system.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object error;
    private Instant timestamp;
    private String path;

    public static <T> ApiResponse<T> ok(T data, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static <T> ApiResponse<T> ok(T data, String message, String path) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static ApiResponse<Void> ok(String path) {
        return ApiResponse.<Void>builder()
                .success(true)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }

    public static ApiResponse<Object> error(Object err, String path) {
        return ApiResponse.builder()
                .success(false)
                .error(err)
                .timestamp(Instant.now())
                .path(path)
                .build();
    }
}
