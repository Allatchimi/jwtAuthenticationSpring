package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "catId")
    private Integer categoryId;
    private String categoryName;
    private  String description;



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category() {
    }

    public Category(Integer categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    public Category(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
