package com.example.studentms.service;

import com.example.studentms.entity.Student;
import java.util.List;

public interface StudentService {
    List<Student>findAll();
    Student findById(Integer id);
    int addStudent(Student student);
    int updateStudent(Student student);
    int deleteById(Integer id);
    List<Student>findByPage(Integer pageNum,Integer pageSize);
    List<Student> findByName(String name);
    List<Student>findByAge(Integer age);
}