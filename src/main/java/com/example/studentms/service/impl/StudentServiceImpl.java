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

    private static final String STUDENT_LIST_KEY="student:list:all";
    //查询全部学生
    @Override
    public List<Student>findAll(){
        String key=STUDENT_LIST_KEY;

        String studentListJson=stringRedisTemplate.opsForValue().get(key);
        if(studentListJson!=null&&!studentListJson.isEmpty()){
            try{
                return objectMapper.readValue(studentListJson,objectMapper.getTypeFactory().constructCollectionType(List.class,Student.class));
            }catch(Exception e){
                throw new RuntimeException("Redis 学生列表反序列化失败");
            }
        }
        
        List<Student> students=studentMapper.findAll();
        try{
            String json=objectMapper.writeValueAsString(students);
            stringRedisTemplate.opsForValue().set(key,json,10,TimeUnit.MINUTES);
        }catch(Exception e){
            throw new RuntimeException("Redis 学生列表序列化失败");
        }
        return students;
    }

    //通过id查找学生
    @Override
    public Student findById(Integer id){
        String key="student:"+id;

        String studentJson=stringRedisTemplate.opsForValue().get(key);
        if(studentJson!=null&&!studentJson.isEmpty()){
            if("null".equals(studentJson)){
                return null;
            }
            try{
                return objectMapper.readValue(studentJson,Student.class);
            }catch(Exception e){
                throw new RuntimeException("Redis 数据反序列化失败");
            }
        }

        Student student=studentMapper.findById(id);

        try{
            if(student==null){
                stringRedisTemplate.opsForValue().set(key,"null",2,TimeUnit.MINUTES);
                return null;
            }

            String json=objectMapper.writeValueAsString(student);
            stringRedisTemplate.opsForValue().set(key,json,10,TimeUnit.MINUTES);
        }catch(Exception e){
            throw new RuntimeException("Redis 数据序列化失败");
        }
        return student;
    }

    //插入学生
    @Override
    public int addStudent(Student student){
        int rows=studentMapper.addStudent(student);
        if(rows>0)
            stringRedisTemplate.delete("student:list:all");
        return rows;
    }

    //更新学生
    @Override
    public int updateStudent(Student student){
        int rows=studentMapper.updateStudent(student);
        if(rows>0){
            stringRedisTemplate.delete("student:"+student.getId());
            stringRedisTemplate.delete("student:list:all");
        }
        return rows;
    }

    //删除学生
    @Override
    public int deleteById(Integer id){
        int rows=studentMapper.deleteById(id);
        if(rows>0){
            stringRedisTemplate.delete("student:"+id);
            stringRedisTemplate.delete("student:list:all");
        }
        return rows;
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
