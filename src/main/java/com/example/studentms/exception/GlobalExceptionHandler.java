package com.example.studentms.exception;

import com.example.studentms.result.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public Result<String>handleBusinessException(BusinessException e){
        return Result.fail(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<String>handleException(Exception e){
        e.printStackTrace();
        return Result.fail(500,"服务器内部错误");
    }
}
