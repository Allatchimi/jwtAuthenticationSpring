package com.kidami.security.models;

import jakarta.persistence.*;

import java.util.Collection;


@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "categoryId")
    private Long categoryId;
    @Column(name = "categoryName")
    private String categoryName;
    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    private Collection<Cour> cour;

    public Category() {
    }

    public Category(String categoryName, Collection<Cour> cour) {
        this.categoryName = categoryName;
        this.cour = cour;
    }


    public Category(Long categoryId, String categoryName, Collection<Cour> cour) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.cour = cour;
    }


    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Collection<Cour> getCour() {
        return cour;
    }

    public void setCour(Collection<Cour> cour) {
        this.cour = cour;
    }
}
