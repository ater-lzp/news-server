package com.news.domain;

import lombok.Data;

@Data
public class BusinessException extends Exception {
    private int code;
    private String type;
/**
     * @param code 状态码
     * @param type 异常类型
     * @param message 异常信息
     */
    public BusinessException(int code, String type, String message) {
        super(message);
        this.code = code;
        this.type = type;
    }
}
