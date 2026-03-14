package vn.id.luannv.sapotest.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.id.luannv.sapotest.dto.response.BaseResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public BaseResponse<?> handleAppException(AppException ex) {
        log.info("AppException: {}", ex.getMessage());
        return BaseResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .errorCode(ex.getErrorCode())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> handleException(Exception ex) {
        log.info("Exception: {}", ex.getMessage());
        return BaseResponse.builder()
                .success(false)
                .message("Unexpected error")
                .errorCode("INTERNAL_ERROR")
                .build();
    }
}
