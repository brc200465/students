package com.example.studentms.service.impl;

import com.example.studentms.entity.Student;
import com.example.studentms.mapper.StudentMapper;
import com.example.studentms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentMapper studentMapper;

    //查询全部学生
    @Override
    public List<Student>findAll(){
        return studentMapper.findAll();
    }

    //通过id查找学生
    @Override
    public Student findById(Integer id){
        return studentMapper.findById(id);
    }

    //插入学生
    @Override
    public int addStudent(Student student){
        return studentMapper.addStudent(student);
    }

    //更新学生
    @Override
    public int updateStudent(Student student){
        return studentMapper.updateStudent(student);
    }

    //删除学生
    @Override
    public int deleteById(Integer id){
        return studentMapper.deleteById(id);
    }

    //分页查询学生
    @Override
    public List<Student>findByPage(Integer pageNum,Integer pageSize){
        Integer offset=(pageNum-1)*pageSize;
        return studentMapper.findByPage(offset,pageSize);
    }

    //按姓名查询学生
    @Override
    public List<Student>findByName(String name){
        return studentMapper.findByName(name);
    }

    //按年龄查询学生
    @Override
    public List<Student>findByAge(Integer age){
        return studentMapper.findByAge(age);
    }
}
