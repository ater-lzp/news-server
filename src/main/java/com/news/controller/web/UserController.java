package com.news.controller.web;

import com.news.domain.BusinessException;
import com.news.domain.Response;
import com.news.domain.User;
import com.news.service.web.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
@Controller
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    // 处理参数缺失异常
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Response handleMissingParams(MissingServletRequestParameterException ex) {
        return new Response(400,"参数错误: " + ex.getParameterName(),"error");
    }

//    处理自定义异常
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Response handleBusinessException(BusinessException e){
        return new Response(e.getCode(),e.getType() + ": " + e.getMessage(),"error");
    }

    //    通用异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Response catchError(Exception e){
        return new Response(500,"服务器内部错误："+e.getMessage(),"error");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Response<User> login(@RequestParam("username") String username, @RequestParam("password") String password, HttpSession session) throws Exception {
        return userService.login(username,password ,session);
    }

}
