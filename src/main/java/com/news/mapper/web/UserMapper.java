package com.news.mapper.web;

import com.news.domain.Register;
import com.news.domain.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper {

    User login(@Param("user_name") String user_name, @Param("user_password") String user_password);
    Register register(@Param("user_name") String user_name, @Param("user_password") String user_password);
    List<User> searchUsers(@Param("user_name") String user_name);

}
