package com.example.comunity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiResponse {

    private HttpStatus status;

    private Object dtoInfo;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime localDateTime;

    private ApiResponse() {
        localDateTime = LocalDateTime.now();
    }

    public ApiResponse(final HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiResponse(final HttpStatus status, final Object dtoInfo) {
        this();
        this.status = status;
        this.dtoInfo = dtoInfo;
    }
}
