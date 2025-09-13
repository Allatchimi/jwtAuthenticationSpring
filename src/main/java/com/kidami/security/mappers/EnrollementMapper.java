package com.kidami.security.mappers;

import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.models.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface EnrollementMapper {
    @Mapping(source = "cour.name", target = "courName")
    @Mapping(source = "student.name", target = "studentName")
    EnrollementDTO toDTO(Enrollment enrollment);
    Enrollment toEntity(EnrollementDTO enrollementDTO);
}
