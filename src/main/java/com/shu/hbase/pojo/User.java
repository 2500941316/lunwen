package com.shu.hbase.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {
    //**工号**,姓名,部门,身份
    private String userId;
    private String name;
    private String department;//本人所在部门
    private String duty;//身份

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }
}
