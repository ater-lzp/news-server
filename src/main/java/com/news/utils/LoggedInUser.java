package com.news.utils;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import lombok.Data;

@Data
public class LoggedInUser implements HttpSessionBindingListener {
    private final String username;
    private final SessionManager sessionManager;
    private transient HttpSession session;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        this.session = event.getSession();
    }

    public LoggedInUser(String username, SessionManager sessionManager) {
        this.username = username;
        this.sessionManager = sessionManager;
    }
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        sessionManager.removeSessionByUsername(username);
        this.session = null;
    }
}
