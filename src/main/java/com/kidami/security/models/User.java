package com.kidami.security.models;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name= "user")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="userID")
    private Integer id;
    @Column(name="name", length =50 )
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name="password", length =200)
    private String password;
    private String provider;
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING) // ou EnumType.ORDINAL si vous préférez
    private Set<Role> roles = new HashSet<>(); // Utilisation d'un ensemble pour les rôles


    public User() {
    }

    public User(Integer id, String name, String provider, String email, String password, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.roles = roles;
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
    public Set<Role> getRoles() {
        return roles;
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
                ", role=" + roles +
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

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
