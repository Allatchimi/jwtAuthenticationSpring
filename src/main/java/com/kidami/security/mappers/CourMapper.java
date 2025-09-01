package com.kidami.security.mappers;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourDeteailDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.models.Category;
import com.kidami.security.models.Cour;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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