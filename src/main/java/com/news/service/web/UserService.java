package com.news.service.web;

import com.news.domain.Response;
import com.news.domain.User;
import jakarta.servlet.http.HttpSession;

import java.util.List;

public interface UserService {
    Response<User> login(String user_name, String user_password, HttpSession session) throws Exception;
    Response register(String user_name, String user_password) throws Exception;
    Response<List<User>> searchUsers(String user_name) throws Exception;
}
