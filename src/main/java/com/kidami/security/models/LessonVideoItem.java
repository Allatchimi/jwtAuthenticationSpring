package com.kidami.security.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lesson_video_items")
public class LessonVideoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 255, message = "Le nom doit contenir entre 2 et 255 caractères")
    @Column(nullable = false, length = 255)
    private String name;
    @NotBlank(message = "L'URL est obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String url;
    @Column(columnDefinition = "TEXT")
    private String thumbnail;
    @Column(nullable = false)
    private Integer duration = 0;
    @Column(nullable = false)
    private Integer orderIndex = 0;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    @JsonBackReference
    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    private Lesson lesson;
    // Méthode utilitaire pour la relation
    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
        if (lesson != null && !lesson.getVideos().contains(this)) {
            lesson.getVideos().add(this);
        }
    }
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        LessonVideoItem that = (LessonVideoItem) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}