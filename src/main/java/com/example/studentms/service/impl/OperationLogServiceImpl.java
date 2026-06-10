package com.example.studentms.service.impl;

import com.example.studentms.entity.OperationLog;
import com.example.studentms.mapper.OperationLogMapper;
import com.example.studentms.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Async
    @Override
    public void saveLog(Integer userId,
                        String username,
                        String operationType,
                        String operationDesc,
                        String requestIp,
                        Boolean success) {
        try {
            System.out.println("异步日志线程：" + Thread.currentThread().getName());

            OperationLog log = new OperationLog(
                    userId,
                    username,
                    operationType,
                    operationDesc,
                    requestIp,
                    success
            );

            operationLogMapper.addLog(log);
        } catch (Exception e) {
            System.out.println("写入操作日志失败：" + e.getMessage());
        }
    }
}