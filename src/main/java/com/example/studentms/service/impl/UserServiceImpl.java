package com.example.studentms.service.impl;

import com.example.studentms.entity.User;
import com.example.studentms.mapper.UserMapper;
import com.example.studentms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
    @Override
    public User findByUsername(String username){
        return userMapper.findByUsername(username);
    }

    @Override
    public int register(User user){
        String encodedPassword=passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userMapper.addUser(user);
    }

    @Override
    public User login(String username,String password){
        User user=userMapper.findByUsername(username);
        if(user==null)
            return null;
        if(!passwordEncoder.matches(password,user.getPassword()))
            return null;
        return user;
    }
}
