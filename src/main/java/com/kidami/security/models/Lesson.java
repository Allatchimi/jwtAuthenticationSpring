package com.kidami.security.models;

import jakarta.persistence.*;

import java.util.List;
import java.util.Objects;

@Entity
public class Lesson {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;
    private  String name;
    //private Cour cour;
    private String thumbnail;
    private String description;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LessonVideoItem> video;


    public Lesson() {
    }


    public Lesson(Integer id, String name, String thumbnail, String description, List<LessonVideoItem> video) {
        this.id = id;
        this.name = name;
        this.thumbnail = thumbnail;
        this.description = description;
        this.video = video;
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<LessonVideoItem> getVideo() {
        return video;
    }

    public void setVideo(List<LessonVideoItem> video) {
        this.video = video;
    }


}
