package com.kidami.security.models;

import jakarta.persistence.*;

@Entity
@Table(name= "user")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userID")
    private Integer id;
    @Column(name="name", length =50 )
    private String name;
    @Column(name="email", length =100 )
    private String email;
    @Column(name="password", length =200)
    private String password;
    private String provider;
    @Column(name="role", length = 50)
    @Enumerated(EnumType.STRING)
    private String role;

    public User() {
    }

    public User(Integer id, String name, String provider, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.role = role;
    }

    public User(String name, String provider, String email, String role) {
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String getRole() {
        return role;
    }
    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
