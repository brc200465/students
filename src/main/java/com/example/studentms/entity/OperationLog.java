package com.example.studentms.entity;

import java.time.LocalDateTime;

public class OperationLog {
    private Integer id;
    private Integer studentId;
    private String operation;
    private String content;
    private LocalDateTime createTime;

    public OperationLog(){}
    public OperationLog(Integer id,Integer studentId,String operation,String content,LocalDateTime createTime){
        this.id=id;
        this.studentId=studentId;
        this.operation=operation;
        this.content=content;
        this.createTime=createTime;
    }

    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id=id;
    }

    public Integer getStudentId(){
        return studentId;
    }
    public void setStudentId(Integer studentId){
        this.studentId=studentId;
    }

    public String getOperation(){
        return operation;
    }
    public void setOperation(String operation){
        this.operation=operation;
    }

    public String getContent(){
        return content;
    }
    public void setContent(String content){
        this.content=content;
    }

    public LocalDateTime getCreateTime(){
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime){
        this.createTime=createTime;
    }
}
