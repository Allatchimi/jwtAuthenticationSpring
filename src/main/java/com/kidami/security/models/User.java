package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name= "user")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userID")
    private Long id;
    @Column(name="firstname", length =50 )
    private String firstname;
    @Column(name="lastname", length = 50)
    private String lastname;
    @Column(name="email", length =100 )
    private String email;
    @Column(name="password", length =200)
    private String password;
    private String provider;
    @Column(name="role", length = 50)
    @Enumerated(EnumType.STRING)
    private Role role;

    public User() {
    }

    public User(Long id, String firstname, String provider,String lastname, String email, String password, Role role) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.role = role;
    }

    public User(String firstname, String provider,String lastname, String email, Role role) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.provider = provider;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
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
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
