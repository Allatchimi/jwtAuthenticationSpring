package com.kidami.security.services;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Enrollment;
import com.kidami.security.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CourService {

    CourDTO addCour(CourSaveDTO courSaveDTO, String teacherUsername, MultipartFile file);
    List<CourDTO> getAllCours();
    CourDeteailDTO courtDetails(Integer courId);
    CourDTO updateCour(CourUpdateDTO courUpdateDTO);
    boolean deleteCour(Long id);

    List<Cour> getPopularCourses();

    Enrollment enrollToCourse(Long courseId, String username);

    List<Cour> getUserCourses(String username);

    List<Cour> getTeacherCourses(String username);
}
