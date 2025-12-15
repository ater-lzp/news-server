package com.news.utils;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private Map<String, LoggedInUser> sessionMap = new ConcurrentHashMap<>();

    /**
     * 注册用户会话，实现单点登录。
     * @param username 用户名
     * @param session  新的 HttpSession
     */
    public void registerSession(String username, HttpSession session) {
        // 1. 如果用户已登录，先踢掉旧的会话
        LoggedInUser oldUser = sessionMap.get(username);
        if (oldUser != null) {
            HttpSession oldSession = oldUser.getSession();
            try {
                if (oldSession != null) {
                    oldSession.invalidate();
                }
            } catch (IllegalStateException e) {
                // 会话已失效，属于正常情况
            } catch (Exception e) {
                // 记录异常但不中断主流程
            }
            this.removeSessionByUsername(username);
        }
        LoggedInUser newUser = new LoggedInUser(username, this);
        sessionMap.put(username, newUser);
        session.setAttribute("loggedInUser", newUser);
    }

    /**
     * 移除用户会话记录（由 LoggedInUser 监听器调用）。
     * @param username 用户名
     */
    public void removeSessionByUsername(String username) {
        // 从 map 中移除即可，HttpSession 的销毁由调用者（或监听器触发）负责
        sessionMap.remove(username);
    }

    /**
     * 检查用户是否已登录
     */
    public boolean isUserLoggedIn(String username) {
        return sessionMap.containsKey(username);
    }

    /**
     * 获取已登录用户的数量
     */
    public int getLoggedInUserCount() {
        return sessionMap.size();
    }
}
