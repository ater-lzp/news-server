package com.news.domain;

import lombok.Data;

@Data
public class Response<T> {
    private int code;
    private String msg;
    private T data;
    private String actionType;

    public Response() {
    }

    /**
     * 构造函数
     * @param code 状态码
     * @param msg 提示信息
     * @param data  数据
     * @param actionType 操作类型
     */
    public Response( int code, String msg,String actionType,T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.actionType = actionType;
    }

    /**
     * 构造函数
     * @param code 状态码
     * @param msg 提示信息
     * @param actionType 操作类型
     */
    public Response( int code, String msg ,String actionType) {
        this.code = code;
        this.msg = msg;
        this.actionType = actionType;
    }
}
