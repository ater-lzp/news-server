package com.news.mapper.web;

import com.news.domain.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {

    public User login(@Param("user_name") String user_name,@Param("user_password") String user_password );

}
