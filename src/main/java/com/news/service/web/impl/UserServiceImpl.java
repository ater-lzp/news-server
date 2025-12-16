package com.news.service.web.impl;

import com.news.domain.BusinessException;
import com.news.domain.Register;
import com.news.domain.Response;
import com.news.domain.User;
import com.news.mapper.web.UserMapper;
import com.news.service.web.UserService;
import com.news.utils.SessionManager;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SessionManager sessionManager;


    @Override
    public Response<User> login(String user_name, String user_password , HttpSession session) throws Exception {
        if(user_name.isEmpty() || user_password.isEmpty() )
            throw new BusinessException(400,"参数错误","参数不能为空" );
        User user = userMapper.login(user_name,user_password);
        if (user != null){
            sessionManager.registerSession(user.getUser_id(),user.getUser_name(),session);
            return  new Response<>(200,"登录成功", "ok",user);
        }
        else
            throw new BusinessException(401,"登录失败","账号或者密码错误" );
    }

    @Override
    public Response register(String user_name, String user_password) throws Exception {
        if(user_name.isEmpty() || user_password.isEmpty() )
            throw new BusinessException(400,"参数错误","参数不能为空" );
        Register reg = userMapper.register(user_name,user_password);
        if (reg == null ){
            throw new BusinessException(500,"注册失败","未知错误" );
        }else if (reg.getType() == 1){
            return new Response<>(200,reg.getMsg(), "ok",null);
        }else {
            throw new BusinessException(401,"注册失败",reg.getMsg());
        }
    }

    @Override
    public Response<List<User>> searchUsers(@Param("user_name") String user_name) throws Exception {
        if(user_name.isEmpty() )
            throw new BusinessException(400,"参数错误","参数不能为空" );
        List<User> users = userMapper.searchUsers(user_name);
        Response<List<User>> res =  new Response<>(200,"查询成功","ok", users);

        return res;
    }
}
