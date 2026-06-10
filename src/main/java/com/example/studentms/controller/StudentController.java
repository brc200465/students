package com.example.studentms.controller;

import com.example.studentms.entity.Student;
import com.example.studentms.entity.StudentQuery;
import com.example.studentms.exception.BusinessException;
import com.example.studentms.result.Result;
import com.example.studentms.service.OperationLogService;
import com.example.studentms.service.StudentService;
import com.example.studentms.vo.CursorPageResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private StudentService studentService;

    @GetMapping
    public Result<List<Student>> findAll() {
        List<Student> students = studentService.findAll();
        if (students.size() == 0) {
            return Result.fail(404, "没有任何学生存在");
        }
        return Result.success(students);
    }

    @GetMapping("/{id}")
    public Result<Student> findById(@PathVariable Integer id) {
        Student student = studentService.findById(id);
        if (student == null) {
            throw new BusinessException(404, "没有找到该学生");
        }

        return Result.success(student);
    }

    @GetMapping("/page")
    public Result<List<Student>> findByPage(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        if (pageNum < 1) {
            return Result.fail(400, "pageNum不能小于1");
        }
        if (pageSize < 1) {
            return Result.fail(400, "pageSize不能小于1");
        }
        List<Student> students = studentService.findByPage(pageNum, pageSize);
        if (students.size() == 0) {
            return Result.fail(404, "没有任何学生存在");
        }
        return Result.success(students);
    }

    @PostMapping
    public Result<String> addStudent(@RequestBody Student student, HttpServletRequest request) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return Result.fail(500, "name不能为空");
        }
        if (student.getAge() == null || student.getAge() < 0) {
            return Result.fail(500, "age不能小于0");
        }
        int rows = studentService.addStudent(student);

        if (rows < 0) {
            throw new BusinessException(500, "新增失败");
        }

        operationLogService.saveLog(
                getCurrentUserId(request),
                getCurrentUsername(request),
                "STUDENT_ADD",
                "新增学生:" + student.getName(),
                request.getRemoteAddr(),
                true
        );

        return Result.success("新增学生成功");
    }

    @PutMapping
    public Result<String> updateStudent(@RequestBody Student student, HttpServletRequest request) {
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            return Result.fail(500, "name不能为空");
        }
        if (student.getAge() == null || student.getAge() < 0) {
            return Result.fail(500, "age不能小于0");
        }
        int rows = studentService.updateStudent(student);
        if (rows < 0) {
            throw new BusinessException(500, "修改失败");
        }
        operationLogService.saveLog(
                getCurrentUserId(request),
                getCurrentUsername(request),
                "STUDENT_UPDATE",
                "修改学生ID:" + student.getId(),
                request.getRemoteAddr(),
                true
        );
        return Result.success("修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteStudent(@PathVariable Integer id, HttpServletRequest request) {
        int rows = studentService.deleteById(id);
        if (rows < 0) {
            throw new BusinessException(500, "删除失败");
        }

        operationLogService.saveLog(
                getCurrentUserId(request),
                getCurrentUsername(request),
                "STUDENT_DELETE",
                "删除学生:" + id,
                request.getRemoteAddr(),
                true
        );
        return Result.success("删除成功", null);
    }

    @GetMapping("/searchbyname")
    public Result<List<Student>> findByName(@RequestParam String name) {
        if (name == null || name.trim().isEmpty()) {
            return Result.fail(500, "name不能为空");
        }
        List<Student> students = studentService.findByName(name);
        if (students.size() == 0) {
            return Result.fail(404, "该姓名学生不存在");
        }
        return Result.success(students);
    }

    @GetMapping("/searchbyage")
    public Result<List<Student>> findByAge(@RequestParam Integer age) {
        if (age == null || age < 0) {
            return Result.fail(500, "age不能小于0");
        }
        List<Student> students = studentService.findByAge(age);
        if (students.size() == 0) {
            return Result.fail(404, "该年龄学生不存在");
        }
        return Result.success(students);
    }

    @GetMapping("/search")
    public Result<List<Student>> search(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) Integer age) {
        StudentQuery query = new StudentQuery();
        query.setName(name);
        query.setAge(age);

        List<Student> students = studentService.search(query);
        return Result.success(students);
    }

    @GetMapping("/scroll")
    public Result<CursorPageResult<Student>> findByCursor(
            @RequestParam(defaultValue = "0") Integer lastId,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        CursorPageResult<Student> result = studentService.findByCursor(lastId, pageSize);
        return Result.success(result);
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userId = request.getAttribute("loginUserId");

        if (userId == null) {
            return null;
        }

        if (userId instanceof Integer) {
            return (Integer) userId;
        }
        return Integer.valueOf(userId.toString());
    }

    private String getCurrentUsername(HttpServletRequest request) {
        Object username = request.getAttribute("loginUsername");
        return username == null ? null : username.toString();
    }
}
