package com.example.studentms.mapper;

import com.example.studentms.entity.OperationLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper {
    
    @Insert("insert into operation_log(student_id,operation,content)"+
             "values(#{studentId},#{operation},#{content})")
    int addLog(OperationLog log);
}
