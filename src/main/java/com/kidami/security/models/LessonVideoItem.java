package com.kidami.security.models;

import jakarta.persistence.*;

@Entity
public class LessonVideoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String url;
    private String thumbnail;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    public LessonVideoItem() {
    }

    public LessonVideoItem(Integer id, String name, String url, String thumbnail, Lesson lesson) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.thumbnail = thumbnail;
        this.lesson = lesson;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }
}
