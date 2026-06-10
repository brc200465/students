package com.example.studentms.mapper;

import com.example.studentms.entity.Student;
import com.example.studentms.entity.StudentQuery;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    //查询全部学生
    @Select("select id,name,age from student")
    List<Student>findAll();

    //按id查询学生
    @Select("select id,name,age from student where id=#{id}")
    Student findById(Integer id);

    //插入学生
    @Insert("insert into student(id,name,age) values(#{id},#{name},#{age})")
    int addStudent(Student student);

    //更新学生OperationLogMapper.java
    @Update("update student set name=#{name},age=#{age} where id=#{id}")
    int updateStudent(Student student);

    //删除学生
    @Delete("delete from student where id=#{id}")
    int deleteById(Integer id);

    //分页查询学生
    @Select("select * from student limit #{offset},#{pageSize}")
    List<Student>findByPage(@Param("offset")Integer offset,@Param("pageSize")Integer pageSize);

    //按姓名查询学生
    @Select("select * from student where name=#{name}")
    List<Student>findByName(String name);

    //按年龄查询学生
    @Select("select * from student where age=#{age}")
    List<Student>findByAge(Integer age);

    //动态查询
    List<Student>search(StudentQuery query);

    //深分页查询
    @Select("SELECT * FROM student "+
        "where id>#{lastId} "+
        "order by id asc "+
        "limit #{limit}"
    )
    List<Student>findByCursor(@Param("lastId") Integer lastId,@Param("limit") Integer limit);
}
