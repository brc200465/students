package com.example.studentms.service.impl;

import com.example.studentms.entity.Student;
import com.example.studentms.mapper.StudentMapper;
import com.example.studentms.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
   
    @Autowired
    private ObjectMapper objectMapper;

    //查询全部学生
    @Override
    public List<Student>findAll(){
        return studentMapper.findAll();
    }

    //通过id查找学生
    @Override
    public Student findById(Integer id){
        String key="student:"+id;

        String studentJson=stringRedisTemplate.opsForValue().get(key);
        if(studentJson!=null&&!studentJson.isEmpty()){
            try{
                return objectMapper.readValue(studentJson,Student.class);
            }catch(Exception e){
                throw new RuntimeException("Redis 数据反序列化失败");
            }
        }
        Student student=studentMapper.findById(id);
        if(student!=null){
            try{
                String json=objectMapper.writeValueAsString(student);
                stringRedisTemplate.opsForValue().set(key,json,10,TimeUnit.MINUTES);
            }catch(Exception e){
                throw new RuntimeException("Redis 数据化序列失败");
            }
        }
        return student;
    }

    //插入学生
    @Override
    public int addStudent(Student student){
        return studentMapper.addStudent(student);
    }

    //更新学生
    @Override
    public int updateStudent(Student student){
        int rows=studentMapper.updateStudent(student);
        if(rows>0)
            stringRedisTemplate.delete("student:"+student.getId());
        return rows;
    }

    //删除学生
    @Override
    public int deleteById(Integer id){
        int rows=studentMapper.deleteById(id);
        if(rows>0)
            stringRedisTemplate.delete("student:"+id);
        return rows;
    }

    //分页查询学生
    @Override
    public List<Student>findByPage(Integer pageNum,Integer pageSize){
        Integer offset=(pageNum-1)*pageSize;
        return studentMapper.findByPage(offset,pageSize);
    }

    //按姓名查询学�?
    @Override
    public List<Student>findByName(String name){
        return studentMapper.findByName(name);
    }

    //按年龄查询学�?
    @Override
    public List<Student>findByAge(Integer age){
        return studentMapper.findByAge(age);
    }
}
