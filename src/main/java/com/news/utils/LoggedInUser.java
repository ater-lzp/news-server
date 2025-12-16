package com.news.utils;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionBindingEvent;
import jakarta.servlet.http.HttpSessionBindingListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class LoggedInUser implements HttpSessionBindingListener {
    private final int uid;
    private final String username; // 保留 username 用于显示，但用 uid 作为标识
    private final SessionManager sessionManager;
    private transient HttpSession session;

    @Override
    public void valueBound(HttpSessionBindingEvent event) {
        this.session = event.getSession();
        log.debug("用户 {} (uid: {}) 的会话已绑定到 HttpSession: {}", username, uid, session.getId());
    }

    public LoggedInUser(int uid, String username, SessionManager sessionManager) {
        this.uid = uid;
        this.username = username;
        this.sessionManager = sessionManager;
    }
    
    @Override
    public void valueUnbound(HttpSessionBindingEvent event) {
        try {
            // 清理会话属性 - 先检查会话是否有效
            HttpSession session = event.getSession();
            if (session != null) {
                try {
                    // 检查会话是否有效，通过尝试获取 creation time
                    session.getCreationTime();
                    session.removeAttribute("uid");
                    session.removeAttribute("username");
                    log.debug("用户 {} (uid: {}) 的会话已解绑: {}", username, uid, session.getId());
                } catch (IllegalStateException e) {
                    // 会话已失效，属于正常情况
                    log.debug("用户 {} (uid: {}) 的会话已失效: {}", username, uid, e.getMessage());
                }
            }
            
            // 从 sessionManager 中移除 - 无论会话是否有效都要执行
            sessionManager.removeSessionByUid(uid);
            
        } catch (Exception e) {
            log.error("解绑用户 {} (uid: {}) 会话时发生异常", username, uid, e);
        } finally {
            this.session = null;
        }
    }
}
