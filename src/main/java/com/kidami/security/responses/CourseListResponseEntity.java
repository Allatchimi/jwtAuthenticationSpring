package com.kidami.security.responses;

import lombok.Data;

@Data
public class CourseListResponseEntity<T> {
    private String code;
    private String msg;
    private T data;

    public CourseListResponseEntity(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // Getters et Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
