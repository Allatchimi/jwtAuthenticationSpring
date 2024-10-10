package com.kidami.security.responses;

import com.kidami.security.models.Cour;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CourseListResponseEntity {

    private Integer code;
    private String msg;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "course_list_id")
    private List<Cour> data = new ArrayList<>();

    public CourseListResponseEntity() {}

    public CourseListResponseEntity(Integer code, String msg, List<Cour> data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<Cour> getData() {
        return data;
    }

    public void setData(List<Cour> data) {
        this.data = data;
    }
}
