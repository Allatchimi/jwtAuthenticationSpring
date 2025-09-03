package com.kidami.security.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lesson_video_items")
public class LessonVideoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 255, message = "Le nom doit contenir entre 2 et 255 caractères")
    @Column(nullable = false, length = 255)
    private String name;
    @NotBlank(message = "L'URL est obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
    @Column(columnDefinition = "TEXT")
    private String thumbnail;
    // Ajouter la durée et l'ordre
    private Integer duration; // en secondes
    private Integer orderIndex; // ordre d'affichage
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonBackReference // ← Évite les boucles JSON
    @Setter(AccessLevel.NONE)
    private Lesson lesson;

    // Méthode utilitaire pour la relation
    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
        if (lesson != null && !lesson.getVideos().contains(this)) {
            lesson.getVideos().add(this);
        }
    }
}