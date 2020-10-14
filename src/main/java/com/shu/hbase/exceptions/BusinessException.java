package com.shu.hbase.exceptions;

public class BusinessException extends RuntimeException {

    public BusinessException(){
        super();
    }

    //用详细信息指定一个异常
    public BusinessException(String message){
        super(message);
    }
}
