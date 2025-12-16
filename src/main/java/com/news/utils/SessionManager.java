package com.news.utils;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SessionManager {

    private final Map<Integer, LoggedInUser> sessionMap = new ConcurrentHashMap<>();

    /**
     * 注册用户会话，实现单点登录。
     * @param uid 用户ID
     * @param username 用户名
     * @param session  新的 HttpSession
     */
    public void registerSession(int uid, String username, HttpSession session) {
        // 1. 如果用户已登录，先踢掉旧的会话
        LoggedInUser oldUser = sessionMap.get(uid);
        if (oldUser != null) {
            HttpSession oldSession = oldUser.getSession();
            try {
                if (oldSession != null) {
                    log.info("用户 {} (uid: {}) 已在其他地方登录，正在使旧会话失效: {}", username, uid, oldSession.getId());
                    oldSession.invalidate();
                }
            } catch (IllegalStateException e) {
                // 会话已失效，属于正常情况
                log.debug("用户 {} (uid: {}) 的旧会话已失效: {}", username, uid, e.getMessage());
            } catch (Exception e) {
                // 记录异常但不中断主流程
                log.error("使旧会话失效时发生异常，用户: {} (uid: {})", username, uid, e);
            }
            // 注意：这里不移除 map 中的记录，因为 LoggedInUser.valueUnbound() 会负责移除
        }
        
        // 2. 创建新的用户会话记录
        LoggedInUser newUser = new LoggedInUser(uid, username, this);
        session.setAttribute("uid", uid);
        session.setAttribute("username", username);
        sessionMap.put(uid, newUser);
        
        log.info("用户 {} (uid: {}) 注册新会话: {}", username, uid, session.getId());
    }

    /**
     * 移除用户会话记录（由 LoggedInUser 监听器调用）。
     * @param uid 用户ID
     */
    public void removeSessionByUid(int uid) {
        LoggedInUser removedUser = sessionMap.remove(uid);
        if (removedUser != null) {
            log.debug("从 sessionMap 中移除用户 (uid: {}): {}", uid, removedUser.getUsername());
        }
    }

    /**
     * 检查用户是否已登录（基于 uid）
     * @param uid 用户ID
     */
    public boolean isUserLoggedIn(int uid) {
        boolean loggedIn = sessionMap.containsKey(uid);
        log.debug("检查用户 (uid: {}) 登录状态: {}", uid, loggedIn);
        return loggedIn;
    }

    /**
     * 获取已登录用户的数量
     */
    public int getLoggedInUserCount() {
        int count = sessionMap.size();
        log.debug("当前已登录用户数量: {}", count);
        return count;
    }

    /**
     * 根据 HttpSession 查找对应的 uid
     * @param session HttpSession 对象
     * @return 对应的 uid，如果找不到返回 -1
     */
    public int getUidBySession(HttpSession session) {
        if (session == null) {
            log.debug("Session 为 null，无法查找 uid");
            return -1;
        }
        
        try {
            // 首先尝试从 session 属性中获取 uid
            Integer sessionUid = (Integer) session.getAttribute("uid");
            if (sessionUid != null) {
                // 验证该用户是否在我们的 sessionMap 中
                if (sessionMap.containsKey(sessionUid)) {
                    LoggedInUser loggedInUser = sessionMap.get(sessionUid);
                    // 进一步验证 session 是否是同一个
                    if (loggedInUser != null && loggedInUser.getSession() == session) {
                        log.debug("通过 session 找到 uid: {}", sessionUid);
                        return sessionUid;
                    }
                }
            }
            
            // 如果直接属性查找失败，遍历 sessionMap 查找匹配的 session
            for (Map.Entry<Integer, LoggedInUser> entry : sessionMap.entrySet()) {
                LoggedInUser loggedInUser = entry.getValue();
                if (loggedInUser != null && loggedInUser.getSession() == session) {
                    log.debug("通过遍历找到 uid: {} 对应 session", entry.getKey());
                    return entry.getKey();
                }
            }
            
            log.debug("未找到对应 session 的 uid");
            return -1;
            
        } catch (IllegalStateException e) {
            // session 已失效
            log.debug("Session 已失效，无法获取 uid: {}", e.getMessage());
            return -1;
        } catch (Exception e) {
            log.error("根据 session 查找 uid 时发生异常", e);
            return -1;
        }
    }

    /**
     * 根据 sessionId 查找对应的 uid
     * @param sessionId session ID
     * @return 对应的 uid，如果找不到返回 -1
     */
    public int getUidBySessionId(String sessionId) {
        if (sessionId == null || sessionId.trim().isEmpty()) {
            log.debug("SessionId 为空，无法查找 uid");
            return -1;
        }
        
        try {
            for (Map.Entry<Integer, LoggedInUser> entry : sessionMap.entrySet()) {
                LoggedInUser loggedInUser = entry.getValue();
                if (loggedInUser != null && loggedInUser.getSession() != null) {
                    HttpSession session = loggedInUser.getSession();
                    try {
                        if (sessionId.equals(session.getId())) {
                            log.debug("通过 sessionId 找到 uid: {}", entry.getKey());
                            return entry.getKey();
                        }
                    } catch (IllegalStateException e) {
                        // 该 session 已失效，跳过
                        log.debug("Session 已失效，跳过: {}", e.getMessage());
                        continue;
                    }
                }
            }
            
            log.debug("未找到对应 sessionId [{}] 的 uid", sessionId);
            return -1;
            
        } catch (Exception e) {
            log.error("根据 sessionId 查找 uid 时发生异常", e);
            return -1;
        }
    }

    /**
     * 根据 uid 获取用户名
     * @param uid 用户ID
     * @return 对应的用户名，如果找不到返回 null
     */
    public String getUsernameByUid(int uid) {
        LoggedInUser loggedInUser = sessionMap.get(uid);
        if (loggedInUser != null) {
            String username = loggedInUser.getUsername();
            log.debug("通过 uid [{}] 找到用户名: {}", uid, username);
            return username;
        }
        
        log.debug("未找到 uid [{}] 对应的用户名", uid);
        return null;
    }
}
