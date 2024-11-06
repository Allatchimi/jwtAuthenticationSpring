package com.kidami.security.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonVideoItemRep {

    private Integer id;           // ID de l'élément vidéo
    private String name;          // Nom de l'élément vidéo
    private String url;           // URL de l'élément vidéo
    private String thumbnail;      // URL de la miniature
    private Integer lesson_id;      // ID de la leçon associée
    private String lessonName;     // Nom de la leçon associée

}
