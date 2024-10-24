package com.kidami.security.services.impl;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;


import com.kidami.security.models.Cour;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.services.CourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourServiceImpl implements CourService {
    @Autowired
    private CourRepository courRepository;


    @Override
    public String addCour(CourSaveDTO courSaveDTO) {
        Cour cour = new Cour();
        cour.setName(courSaveDTO.getName());
               cour.setDescription(courSaveDTO.getDescription());
               cour.setPrice(courSaveDTO.getPrice());
               cour.setVideo(courSaveDTO.getVideo());
                cour.setThumbnail(courSaveDTO.getThumbnail());
                cour.setUserToken(courSaveDTO.getUserToken());
                cour.setAmountTotal(courSaveDTO.getAmountTotal());
                cour.setLessonNum(courSaveDTO.getLessonNum());
                cour.setVideoLen(courSaveDTO.getVideoLen());
                cour.setFollow(courSaveDTO.getFollow());
                cour.setType_id(courSaveDTO.getType_id());
               cour.setDownNum(courSaveDTO.getDownNum());
               cour.setScore(courSaveDTO.getScore());
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
                    c.getName(),
                    c.getDescription(),
                    c.getVideo()
            );
            courDTOList.add(courDTO);
        }
        return courDTOList;
    }

    @Override
    public String updateCour(CourUpdateDTO courUpdateDTO) {
        if(courRepository.existsById(courUpdateDTO.getId())){
            Cour cour= courRepository.getReferenceById(courUpdateDTO.getId());
            cour.setName(courUpdateDTO.getName());
            cour.setDescription(courUpdateDTO.getDescription());
            cour.setVideo(courUpdateDTO.getVideo());

            courRepository.save(cour);

        }else {
            System.out.println("Lesson not Exist");
        }
        return null;
    }

    @Override
    public boolean deleteCour(Integer id) {
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