package com.kidami.security.services.impl;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourDeteailDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;


import com.kidami.security.models.Category;
import com.kidami.security.models.Cour;
import com.kidami.security.repository.CategoryRepository;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.services.CourService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CourServiceImpl implements CourService {
    @Autowired
    private CourRepository courRepository;
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public CourDTO addCour(CourSaveDTO courSaveDTO) {

        Category categorie = categoryRepository.findById(courSaveDTO.getCategorieId())
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée"));

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
        cour.setCategorie(categorie);
        cour.setDownNum(courSaveDTO.getDownNum());
        cour.setScore(courSaveDTO.getScore());
        courRepository.save(cour);

        CourDTO courDTO = new CourDTO();
        courDTO.setId(cour.getId());
        courDTO.setName(cour.getName());
        courDTO.setDescription(cour.getDescription());
        courDTO.setCategorie(cour.getCategorie());

        return courDTO;
    }

    @Override
    public List<CourDTO> getAllCours() {
        List<Cour> getCours =courRepository.findAll();
        List<CourDTO> courDTOList= new ArrayList<>();

        for(Cour c:getCours){
            CourDTO courDTO = new CourDTO(
                    c.getId(),
                    c.getScore(),
                    c.getLessonNum(),
                    c.getVideoLen(),
                    c.getDownNum(),
                    c.getFollow(),
                    c.getCategorie(),
                    c.getUserToken(),
                    c.getName(),
                    c.getDescription(),
                    c.getThumbnail(),
                    c.getVideo(),
                    c.getPrice(),
                    c.getAmountTotal()
            );
            courDTOList.add(courDTO);
        }
        return courDTOList;
    }

    @Override
    public CourDTO updateCour(CourUpdateDTO courUpdateDTO) {
        if(courRepository.existsById(courUpdateDTO.getId())){
            Cour cour= courRepository.getReferenceById(courUpdateDTO.getId());
            cour.setName(courUpdateDTO.getName());
            cour.setDescription(courUpdateDTO.getDescription());
            cour.setThumbnail(courUpdateDTO.getThumbnail());

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

    @Override
    public CourDeteailDTO courtDetails(Integer courId) {

        if (courRepository.existsById(courId)){

            Cour courDet = courRepository.getReferenceById(courId);
            CourDeteailDTO courDeteailDTO = new CourDeteailDTO();

            courDeteailDTO.setId(courDet.getId());
            courDeteailDTO.setName(courDet.getName());
            courDeteailDTO.setDescription(courDet.getDescription());
            courDeteailDTO.setPrice(courDet.getPrice());
            courDeteailDTO.setVideo(courDet.getVideo());
            courDeteailDTO.setThumbnail(courDet.getThumbnail());
            courDeteailDTO.setUserToken(courDet.getUserToken());
            courDeteailDTO.setAmountTotal(courDet.getAmountTotal());
            courDeteailDTO.setLessonNum(courDet.getLessonNum());
            courDeteailDTO.setVideoLen(courDet.getVideoLen());
            courDeteailDTO.setFollow(courDet.getFollow());
            courDeteailDTO.setCategorie(courDet.getCategorie());
            courDeteailDTO.setDownNum(courDet.getDownNum());
            courDeteailDTO.setScore(courDet.getScore());

            return courDeteailDTO;
        }
       return null;
    }
}