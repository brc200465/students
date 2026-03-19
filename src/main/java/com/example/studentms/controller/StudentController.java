package com.example.studentms.controller;

import com.example.studentms.entity.Student;
import com.example.studentms.exception.BusinessException;
import com.example.studentms.result.Result;
import com.example.studentms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController{
    @Autowired
    private StudentService studentService;

    //查询全部学生
    @GetMapping
    public Result<List<Student>> findAll(){
        List<Student>students=studentService.findAll();
        if(students.size()==0)
            return Result.fail(404,"没有任何学生存在");
        return Result.success(students);
    }

    //按id查询学生
    @GetMapping("/{id}")
    public Result<Student> findById(@PathVariable Integer id){
        Student student=studentService.findById(id);
        if(student==null)
            throw new BusinessException(404,"没有找到该学生");

        return Result.success(student);
    }

    //分页查询学生
    @GetMapping("/page")
    public Result<List<Student>>findByPage(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        if(pageNum<1)
            return Result.fail(400,"pageNum不能小于1");
        if(pageSize<1)
            return Result.fail(400,"pageSize不能小于1");
        List<Student>students=studentService.findByPage(pageNum,pageSize);
        if(students.size()==0)
            return Result.fail(404,"没有任何学生存在");
        return Result.success(students);
    }

    //插入学生
    @PostMapping
    public Result<String> addStudent(@RequestBody Student student){
        if(student.getName()==null||student.getName().trim().isEmpty())
            return Result.fail(500,"name不能为空");
        if(student.getAge()==null||student.getAge()<0)
            return Result.fail(500,"age不能小于0");
        int rows=studentService.addStudent(student);
        return rows>0?Result.success("新增成功",null):Result.fail(500,"插入失败");
    }

    //更新学生
    @PutMapping
    public Result<String> updateStudent(@RequestBody Student student){
        if(student.getName()==null||student.getName().trim().isEmpty())
            return Result.fail(500,"name不能为空");
        if(student.getAge()==null||student.getAge()<0)
            return Result.fail(500,"age不能小于0");
        int rows=studentService.updateStudent(student);
        if(rows<0)
            throw new BusinessException(500,"修改失败");
        return Result.success("修改成功",null);
    }

    //删除学生
    @DeleteMapping("/{id}")
    public Result<String> deleteStudent(@PathVariable Integer id){
        int rows=studentService.deleteById(id);
        if(rows<0)
            throw new BusinessException(500,"删除失败");
        return Result.success("删除成功",null);
    }

    //按姓名查询学生
    @GetMapping("/searchbyname")
    public Result<List<Student>> findByName(@RequestParam String name){
        if(name==null||name.trim().isEmpty())
            return Result.fail(500,"name 不能为空");
        List<Student> students=studentService.findByName(name);
        if(students.size()==0)
            return Result.fail(404,"该姓名学生不存在");
        return Result.success(students);
    }

    //按年龄查询学生
    @GetMapping("/searchbyage")
    public Result<List<Student>> findByAge(@RequestParam Integer age){
        if(age==null||age<0)
            return Result.fail(500,"age不能小于0");
        List<Student>students=studentService.findByAge(age);
        if(students.size()==0)
            return Result.fail(404,"该年龄学生不存在");
        return Result.success(students);
    }
}