package com.kidami.security.services;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourDeteailDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;

import java.util.List;


public interface CourService {

    CourDTO addCour(CourSaveDTO courSaveDTO);
    List<CourDTO> getAllCours();
    CourDeteailDTO courtDetails(Integer courId);
    CourDTO updateCour(CourUpdateDTO courUpdateDTO);
    boolean deleteCour(Integer id);
}
