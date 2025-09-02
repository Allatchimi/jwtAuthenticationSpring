package com.kidami.security.mappers;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.models.Category;
import com.kidami.security.models.Cour;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourMapper {
    // Mapping de base
    CourDTO toDTO(Cour cour);
    CourDeteailDTO toDetailDTO(Cour cour);
    Cour fromSaveDTO(CourSaveDTO dto);
    // Méthode default pour gérer le cas spécial
    default Cour createCourFromDTO(CourSaveDTO dto, Category category) {
        Cour cour = fromSaveDTO(dto);    // Mapping automatique des champs
        cour.setCategorie(category);     // Ajout manuel de la catégorie
        return cour;
    }
}