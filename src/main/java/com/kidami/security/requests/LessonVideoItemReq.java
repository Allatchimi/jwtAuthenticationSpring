package com.kidami.security.requests;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonVideoItemReq {
    private Integer id;
    private String name;
    private String url;
    private String thumbnail;

   // @NotNull(message = "L'ID de la leçon ne peut pas être nul.")
   // private Integer lessonId; // ID de la leçon à laquelle cet élément vidéo est associé

}
