package com.kidami.security.requests;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class LoginRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer type;
    private String name;
    private String description;
    private String email;
    private String phone;
    private String avatar;
    private String openId;
    private Integer online;

    public LoginRequestEntity() {}

    public LoginRequestEntity(Integer type, String name, String description, String email, String phone, String avatar, String openId, Integer online) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.openId = openId;
        this.online = online;
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }
}

