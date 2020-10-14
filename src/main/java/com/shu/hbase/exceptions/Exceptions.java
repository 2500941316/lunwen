package com.shu.hbase.exceptions;

public enum Exceptions {

    SERVER_CONNECTION_ERROR("0001", "网络连接异常"),
    SERVER_PARAMSETTING_ERROR("0002", "参数接收异常"),
    SERVER_AUTH_ERROR("0003", "身份认证异常"),
    SERVER_UNAMEISNULL_ERROR("0004", "用户名或密码为空"),
    SERVER_HTTPTYPE_ERROR("0005", "请求类型异常"),
    SERVER_OTHER_ERROR("0099", "未知异常");

    private String ecode;

    private String emsg;

    Exceptions(String ecode, String emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public String getEcode() {
        return ecode;
    }

    public String getEmsg() {
        return emsg;
    }
}