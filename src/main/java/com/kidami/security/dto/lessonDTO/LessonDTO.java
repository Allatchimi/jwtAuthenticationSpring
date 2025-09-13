package com.kidami.security.dto.lessonDTO;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {
    private Long id;
    private String name;
    private Long courId;
    private String courName; // ← Ajouter le nom du cours pour l'affichage
    private String thumbnail;
    private String description;
    private List<LessonVideoItemDTO> videos; // ← Utiliser le DTO, pas le Rep
}
