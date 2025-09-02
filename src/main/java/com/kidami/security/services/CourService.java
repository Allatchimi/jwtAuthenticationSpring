package com.kidami.security.services;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;

import java.util.List;


public interface CourService {

    CourDTO addCour(CourSaveDTO courSaveDTO);
    List<CourDTO> getAllCours();
    CourDeteailDTO courtDetails(Integer courId);
    CourDTO updateCour(CourUpdateDTO courUpdateDTO);
    boolean deleteCour(Integer id);
}
