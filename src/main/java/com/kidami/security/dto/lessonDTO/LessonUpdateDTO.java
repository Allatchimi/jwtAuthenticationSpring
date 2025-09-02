package com.kidami.security.dto.lessonDTO;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonUpdateDTO {
    private Integer id;
    @Size(min = 2, max = 255, message = "Le nom doit contenir entre 2 et 255 caractères")
    private String name;
    @Size(max = 500, message = "La thumbnail ne doit pas dépasser 500 caractères")
    private String thumbnail;
    private String description;
    private Integer courId;
    private List<LessonVideoItemUpdateDTO> videos;
    private List<Integer> videosToDelete;

    // Méthode pour vérifier si au moins un champ est rempli
    public boolean hasUpdates() {
        return name != null || thumbnail != null ||
                description != null || courId != null ||
                videos != null;
    }
}
