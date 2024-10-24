package com.kidami.security.services;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;

import java.util.List;


public interface CourService {

    String addCour(CourSaveDTO courSaveDTO);
    List<CourDTO> getAllCours();
    String updateCour(CourUpdateDTO courUpdateDTO);

    boolean deleteCour(Integer id);
}
