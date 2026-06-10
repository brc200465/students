package com.example.studentms.mapper;

import com.example.studentms.entity.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {

    @Insert("INSERT INTO operation_log(user_id, username, operation_type, operation_desc, request_ip, success) " +
            "VALUES(#{userId}, #{username}, #{operationType}, #{operationDesc}, #{requestIp}, #{success})")
    int addLog(OperationLog log);
}