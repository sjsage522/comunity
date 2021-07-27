package com.example.comunity.exception;

import com.example.comunity.dto.api.ApiResult;
import com.example.comunity.dto.api.ErrorCode;
import com.example.comunity.dto.api.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import static com.example.comunity.dto.api.ApiResult.failed;
import static org.springframework.http.HttpStatus.valueOf;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(HttpMediaTypeException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleHttpMediaTypeException(
            final HttpMediaTypeException ex) {
        log.error("HttpMediaTypeException", ex);

        final ErrorResponse response = ErrorResponse.from(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * HTTP 요청 BODY 형식 불일치
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);

        final ErrorResponse response = ErrorResponse.from(
                ErrorCode.INCORRECT_HTTP_BODY_FORMAT
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * 잘못된 매개변수 요청
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleIllegalArgumentException(
            final IllegalArgumentException ex) {
        log.error("IllegalArgumentException", ex);

        final ErrorResponse response = ErrorResponse.of(
                ex.getMessage(), ErrorCode.ILLEGAL_ARGUMENT
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * servlet request binding 실패
     */
    @ExceptionHandler(ServletRequestBindingException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleServletRequestBindingException(
            final ServletRequestBindingException ex) {
        log.error("ServletRequestBindingException", ex);

        final ErrorResponse response = ErrorResponse.of(
                ex.getMessage(), ErrorCode.INCORRECT_SERVLET_REQUEST
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * 지원하지 않는 HTTP METHOD 요청한 경우
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException", ex);

        final ErrorResponse response = ErrorResponse.from(
                ErrorCode.METHOD_NOT_ALLOWED
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * 요청 url mapping 핸들러를 찾지 못한 경우
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleNoHandlerFoundException(
            final NoHandlerFoundException ex) {
        log.error("handleNoHandlerFoundException", ex);

        final ErrorResponse response = ErrorResponse.from(
                ErrorCode.HANDLER_NOT_FOUND
        );

        return ResponseEntity
                .status(valueOf(response.getStatus()))
                .body(failed(response));
    }

    /**
     * validation 에러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);

        final ErrorResponse response = ErrorResponse.of(
                ex.getBindingResult(), ErrorCode.INCORRECT_FORMAT
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * 애플리케이션 비즈니스 예외들
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleBusinessException(
            final BusinessException ex) {
        log.error("handleBusinessException", ex);

        final ErrorResponse response = ErrorResponse.of(
                ex.getMessage(), ex.getErrorCode());

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }

    /**
     * 나머지 내부 서버 예외
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResult<ErrorResponse>> handleException(
            final Exception ex) {
        log.error("handleException", ex);

        final ErrorResponse response = ErrorResponse.of(
                ex.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR
        );

        return ResponseEntity
                .status(response.getStatus())
                .body(failed(response));
    }
}
