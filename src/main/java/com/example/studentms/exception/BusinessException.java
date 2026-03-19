package com.example.studentms.exception;

public class BusinessException extends RuntimeException{
    private Integer code;
    private String message;

    public BusinessException(Integer code,String message){
        this.code=code;
        this.message=message;
    }

    public Integer getCode(){return code;}

    @Override
    public String getMessage(){return message;}
}
