package com.news.service.web.impl;

import com.news.domain.BusinessException;
import com.news.domain.Response;
import com.news.domain.User;
import com.news.mapper.web.UserMapper;
import com.news.service.web.UserService;
import com.news.utils.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Override
    public Response<User> login(String user_name, String user_password , HttpSession session) throws Exception {
        if(user_name.isEmpty() || user_password.isEmpty() )
            throw new BusinessException(400,"参数错误","参数不能为空" );
        User user = userMapper.login(user_name,user_password);
        if (user != null){
            new SessionManager().registerSession(user_name,session);
            return  new Response<>(200,"登录成功", "ok",user);
        }
        else
            throw new BusinessException(401,"登录失败","账号或者密码错误" );
    }
}
