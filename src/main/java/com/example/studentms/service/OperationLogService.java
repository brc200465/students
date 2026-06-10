package com.example.studentms.service;

public interface OperationLogService {

    void saveLog(Integer userId,
                 String username,
                 String operationType,
                 String operationDesc,
                 String requestIp,
                 Boolean success);
}