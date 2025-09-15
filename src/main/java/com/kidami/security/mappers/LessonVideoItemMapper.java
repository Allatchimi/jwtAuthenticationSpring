package com.kidami.security.mappers;

import com.kidami.security.dto.lessonVideoItemDTO.*;
import com.kidami.security.models.LessonVideoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LessonVideoItemMapper {

    // MÉTHODE DE MISE À JOUR (NOUVELLE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    void updateFromDTO(LessonVideoItemUpdateDTO dto, @MappingTarget LessonVideoItem entity);

    // Méthodes existantes
    @Mapping(source = "lesson.name", target = "lessonName")
    LessonVideoItemDTO toDTO(LessonVideoItem entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    LessonVideoItem fromSaveDTO(LessonVideoItemSaveDTO saveDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    LessonVideoItem fromUpdateDTO(LessonVideoItemUpdateDTO updateDTO);

    @Mapping(target = "lesson", ignore = true)
    LessonVideoItem fromDTO(LessonVideoItemDTO dto);

    @Mapping(source ="lessonName", target = "lesson.name")
    LessonVideoItem toEntity(LessonVideoItemDTO lessonVideoItemDTO);
}