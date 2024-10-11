package com.kidami.security.services.impl;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//@Service
/*
public class CourServiceImpl implements CourService {
    @Autowired
    private CourRepository courRepository;
    @Override
    public String addCour(CourSaveDTO courSaveDTO) {
        Cour cour =new Cour(
                courSaveDTO.getTitle(),
                courSaveDTO.getDescription(),
                courSaveDTO.getContent(),
                courSaveDTO.getCategory()
        );
        courRepository.save(cour);

        return cour.getDescription();
    }

    @Override
    public List<CourDTO> getAllCours() {
        List<Cour> getCours =courRepository.findAll();
        List<CourDTO> courDTOList= new ArrayList<>();

        for(Cour c:getCours){
            CourDTO courDTO = new CourDTO(
                    c.getId(),
                    c.getTitle(),
                    c.getDescription(),
                    c.getCategory(),
                    c.getContent()

            );
            courDTOList.add(courDTO);
        }
        return courDTOList;
    }

    @Override
    public String updateCour(CourUpdateDTO courUpdateDTO) {
        if(courRepository.existsById(courUpdateDTO.getId())){
            Cour cour= courRepository.getReferenceById(courUpdateDTO.getId());
            cour.setTitle(courUpdateDTO.getTitle());
            cour.setDescription(courUpdateDTO.getDescription());
            cour.setContent(courUpdateDTO.getContent());
            cour.setCategory(courUpdateDTO.getCategory());

            courRepository.save(cour);

        }else {
            System.out.println("Lesson not Exist");
        }
        return null;
    }

    @Override
    public boolean deleteCour(Long id) {
        if(courRepository.existsById(id))
        {
            courRepository.deleteById(id);
        }
        else {
            System.out.println("Lesson ID not exist");

        }
        return false;
    }
}
*/