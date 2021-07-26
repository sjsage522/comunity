package com.example.comunity.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResult<T> {

    private final T data;

    @JsonProperty("error")
    private final ErrorResponse errorResponse;

    private ApiResult(T data, ErrorResponse errorResponse) {
        this.data = data;
        this.errorResponse = errorResponse;
    }

    public static <T> ApiResult<T> succeed(T data) {
        return new ApiResult<>(data, null);
    }

    public static <T> ApiResult<T> failed(ErrorResponse errorResponse) {
        return new ApiResult<>(null, errorResponse);
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "data=" + data +
                ", errorResponse=" + errorResponse +
                '}';
    }
}
