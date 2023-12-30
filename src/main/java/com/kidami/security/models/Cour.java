package com.kidami.security.models;

import jakarta.persistence.*;

@Entity
@Table(name= "cour")
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courId")
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @Column(name = "content")
    private String content;

    public Cour() {
    }

   public Cour(Long id, String title, String description, Category category, String content) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.content = content;
    }

   public Cour(String title, String description, String content, Category category) {
            this.title = title;
            this.description = description;
            this.category = category;
            this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}
