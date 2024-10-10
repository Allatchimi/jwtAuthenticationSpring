package com.kidami.security.responses;

import com.kidami.security.models.UserProfile;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class UserLoginResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer code;
    private String msg;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id", referencedColumnName = "id")
    private UserProfile data;

    public UserLoginResponseEntity() {}

    public UserLoginResponseEntity(Integer code, String msg, UserProfile data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public UserProfile getData() {
        return data;
    }

    public void setData(UserProfile data) {
        this.data = data;
    }
}

