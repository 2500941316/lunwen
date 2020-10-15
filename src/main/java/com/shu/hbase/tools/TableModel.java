package com.shu.hbase.tools;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TableModel<T> {
    private int code;
    private String msg;
    private int count;
    private T data;


    public static TableModel success(List data, int total) {
        TableModel tableModel = new TableModel();

        tableModel.setCode(200);
        tableModel.setMsg(null);
        tableModel.setCount(total);
        tableModel.setData(data);

        return tableModel;
    }

    public static TableModel success(String msg) {
        TableModel tableModel = new TableModel();

        tableModel.setCode(200);
        tableModel.setMsg(msg);
        tableModel.setCount(0);

        return tableModel;
    }



    public static TableModel error(String message) {
        TableModel tableModel = new TableModel();
        tableModel.setCode(500);
        tableModel.setData(new ArrayList<>());
        tableModel.setMsg(message);
        tableModel.setCount(0);

        return tableModel;
    }

    public static TableModel error(Integer code, String msg) {
        TableModel tableModel = new TableModel();
        tableModel.setCode(code);
        tableModel.setMsg(msg);

        return tableModel;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
