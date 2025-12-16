package com.news.service.web;

import com.news.domain.Response;
import com.news.domain.User;
import jakarta.servlet.http.HttpSession;

public interface UserService {
    public Response<User> login(String user_name, String user_password , HttpSession session) throws Exception;
}
