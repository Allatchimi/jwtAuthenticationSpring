package com.kidami.security.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lessons")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 255)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 255, message = "Le nom doit contenir entre 2 et 255 caractères")
    private  String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cour_id")
    private Cour cour;
    @Column(length = 500)
    private String thumbnail;
    @Column(columnDefinition = "TEXT")
    private String description;
    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<LessonVideoItem> videos = new ArrayList<>();


    public void addVideoItem(LessonVideoItem videoItem) {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.add(videoItem);
        videoItem.setLesson(this);
    }

    public void removeVideoItem(LessonVideoItem videoItem) {
        if (videos != null) {
            videos.remove(videoItem);
            videoItem.setLesson(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Lesson)) return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
