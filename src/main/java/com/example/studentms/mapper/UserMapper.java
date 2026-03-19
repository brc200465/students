package com.example.studentms.mapper;

import com.example.studentms.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Select("select * from sys_user where username=#{username}")
    User findByUsername(String username);

    @Insert("insert into sys_user(username,password) values(#{username},#{password})")
    int addUser(User user);
}
