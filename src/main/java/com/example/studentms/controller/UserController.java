package com.example.studentms.controller;

import com.example.studentms.entity.User;
import com.example.studentms.exception.BusinessException;
import com.example.studentms.result.Result;
import com.example.studentms.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    //按姓名查询用户
    @GetMapping("/byUsername")
    public Result<User>findByUsername(@RequestParam String username){
        if(username==null||username.trim().isEmpty())
            return Result.fail(400,"username不能为空");
        User user=userService.findByUsername(username);
        if(user==null)
            return Result.fail(404,"找不到该用户");
        
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<String>register(@RequestBody User user){
        if(user.getUsername()==null||user.getUsername().trim().isEmpty()){
            throw new BusinessException(400,"username不能为空");
        }
        if(user.getPassword()==null||user.getPassword().trim().isEmpty()){
            throw new BusinessException(400,"password不能为空");
        }

        User existingUser=userService.findByUsername(user.getUsername());
        if(existingUser!=null)
            throw new BusinessException(400,"用户名已存在");
        int rows=userService.register(user);
        if(rows<=0)
            throw new BusinessException(500,"注册失败");
        return Result.success("注册成功",null);
    }


    @PostMapping("/login")
    public Result<User>login(@RequestBody User user,HttpSession session){
        if(user.getUsername()==null||user.getUsername().trim().isEmpty())
            throw new BusinessException(400,"username不能为空");
        if(user.getPassword()==null||user.getPassword().trim().isEmpty())
            throw new BusinessException(400,"password不能为空");
        
        User loginUser=userService.login(user.getUsername(),user.getPassword());
        if(loginUser==null)
            throw new BusinessException(400,"用户名或密码错误");

        session.setAttribute("loginUserId",loginUser.getId());
        session.setAttribute("loginUsername",loginUser.getUsername());

        loginUser.setPassword(null);
        return Result.success(loginUser);
    }

    @GetMapping("/me")
    public Result<String>me(HttpSession session){
        Object username=session.getAttribute("loginUsername");
        if(username==null)
            return Result.fail(401,"当前未登录");
        return Result.success(username.toString());
    }

    @PostMapping("/logout")
    public Result<String>logout(HttpSession session){
        session.invalidate();
        return Result.success("退出登录成功",null);
    }
}
