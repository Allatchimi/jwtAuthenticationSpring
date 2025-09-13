package com.kidami.security.services;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.models.Enrollment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CourService {

    CourDTO addCour(CourSaveDTO courSaveDTO, String teacherUsername, MultipartFile file);
    List<CourDTO> getAllCours();
    CourDeteailDTO courtDetails(Long courId);
    CourDTO updateCour(CourUpdateDTO courUpdateDTO);
    boolean deleteCour(Long id);
    List<CourDTO> getPopularCourses();
    Enrollment enrollToCourse(Long courseId, String username);
    List<EnrollementDTO> getUserCourses(String username);
    List<CourDTO> getTeacherCourses(String username);
}
