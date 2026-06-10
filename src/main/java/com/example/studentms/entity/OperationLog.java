package com.example.studentms.entity;

import java.time.LocalDateTime;

public class OperationLog {
    private Long id;
    private Integer userId;
    private String username;
    private String operationType;
    private String operationDesc;
    private String requestIp;
    private Boolean success;
    private LocalDateTime createdAt;

    public OperationLog() {
    }

    public OperationLog(Integer userId, String username, String operationType,
                        String operationDesc, String requestIp, Boolean success) {
        this.userId = userId;
        this.username = username;
        this.operationType = operationType;
        this.operationDesc = operationDesc;
        this.requestIp = requestIp;
        this.success = success;
    }

    public Long getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getOperationDesc() {
        return operationDesc;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public Boolean getSuccess() {
        return success;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc;
    }

    public void setRequestIp(String requestIp) {
        this.requestIp = requestIp;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}