package com.kidami.security.dto.lessonDTO;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemSaveDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonSaveDTO {
    private String name;
    private Integer courId;
    private String thumbnail;
    private String description;
    private List<LessonVideoItemSaveDTO> video; // ← DTO spécifique pour la sauvegarde
}
