package com.kidami.security.mappers;

import com.kidami.security.dto.lessonDTO.LessonDTO;
import com.kidami.security.dto.lessonDTO.LessonSaveDTO;
import com.kidami.security.models.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {LessonVideoItemMapper.class, CourMapper.class})
public interface LessonMapper {

    @Mapping(source = "cour.id", target = "courId")
    @Mapping(source = "videos", target = "videos")
    LessonDTO toDTO(Lesson lesson);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cour", ignore = true)
    @Mapping(target = "videos", ignore = true)
    Lesson fromSaveDTO(LessonSaveDTO saveDTO);

    // Méthode pour les listes
    default List<LessonDTO> toDTOList(List<Lesson> lessons) {
        return lessons.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Méthode utilitaire pour extraire l'ID du cours
    @Named("courToCourId")
    static Integer courToCourId(com.kidami.security.models.Cour cour) {
        return Math.toIntExact(cour != null ? cour.getId() : null);
    }
}