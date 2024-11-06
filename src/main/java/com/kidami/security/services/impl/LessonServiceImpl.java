package com.kidami.security.services.impl;

import com.kidami.security.dto.LessonDTO;
import com.kidami.security.dto.LessonSaveDTO;
import com.kidami.security.dto.LessonUpdateDTO;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Lesson;
import com.kidami.security.models.LessonVideoItem;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.repository.LessonRepository;
import com.kidami.security.responses.LessonVideoItemRep;
import com.kidami.security.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourRepository courRepository;
    @Autowired
    public LessonServiceImpl(LessonRepository lessonRepository ,CourRepository courRepository) {
        this.lessonRepository = lessonRepository;
        this.courRepository = courRepository;
    }
    @Override
    public LessonDTO addLesson(LessonSaveDTO lessonSaveDTO) {

        Cour cour = courRepository.findById(lessonSaveDTO.getCourId())
                .orElseThrow(() -> new  RuntimeException("Cour non trouvé"));
        Lesson  lesson = new Lesson();
        lesson.setName(lessonSaveDTO.getName());
        lesson.setDescription(lessonSaveDTO.getDescription());
        lesson.setThumbnail(lessonSaveDTO.getThumbnail());
        lesson.setCour(cour);

        // Convertir la liste de LessonVideoItemReq en LessonVideoItem et définir la leçon associée
        Lesson finalLesson = lesson;
        List<LessonVideoItem> videoItems = lessonSaveDTO.getVideo().stream()
                .map(videoReq -> convertToLessonVideoItem(videoReq, finalLesson)) // passez `lesson` comme argument
                .collect(Collectors.toList());

        lesson.setVideo(videoItems);
        // Sauvegarder l'entité dans le repository
        lesson = lessonRepository.save(lesson);

        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setId(lesson.getId());
        lessonDTO.setName(lesson.getName());
        // Récupère uniquement le nom de 'cour' au lieu de l'objet entier
        if (lesson.getCour() != null) {
           // lessonDTO.setCourNom(lesson.getCour().getName());
           // lessonDTO.setCour(lesson.getCour());
            lessonDTO.setCourId(lesson.getCour().getId());
        }
        lessonDTO.setDescription(lesson.getDescription());
        lessonDTO.setThumbnail(lesson.getThumbnail());

        // Convertir les objets LessonVideoItem en LessonVideoItemRep pour le DTO
        List<LessonVideoItemRep> videoItemReps = lesson.getVideo().stream()
                .map(this::convertToLessonVideoItemDTO)
                .collect(Collectors.toList());
        lessonDTO.setVideo(videoItemReps);

        return lessonDTO;
    }
    // Méthode pour convertir LessonVideoItemDTO en LessonVideoItem
    private LessonVideoItem convertToLessonVideoItem(LessonVideoItemRep dto ,Lesson lesson) {
        LessonVideoItem item = new LessonVideoItem();
        item.setId(dto.getId());
        item.setUrl(dto.getUrl());
        item.setThumbnail(dto.getThumbnail());
        item.setName(dto.getName());
        item.setLesson(lesson);
        return item;
    }

    // Méthode pour convertir LessonVideoItem en LessonVideoItemDTO
    private LessonVideoItemRep convertToLessonVideoItemDTO(LessonVideoItem item) {
        LessonVideoItemRep dto = new LessonVideoItemRep();
        dto.setId(item.getId());
        dto.setUrl(item.getUrl());
        dto.setName(item.getName());
        dto.setThumbnail(item.getThumbnail());

        return dto;
    }

    @Override
    public LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO) {
        return null;
    }

    @Override
    public List<LessonDTO> getAllLesson() {
        List<Lesson> lessons = lessonRepository.findAll();
        List<LessonDTO> lessonDTOList = new ArrayList<>();

        for (Lesson lesson : lessons) {
            LessonDTO lessonDTO = new LessonDTO();
            lessonDTO.setId(lesson.getId());
            lessonDTO.setName(lesson.getName());
            lessonDTO.setDescription(lesson.getDescription());
            lessonDTO.setThumbnail(lesson.getThumbnail());
            if (lesson.getCour() != null) {
               // lessonDTO.setCourNom(lesson.getCour().getName());
                //lessonDTO.setCour(lesson.getCour());
                lessonDTO.setCourId(lesson.getCour().getId());
            }

            // Convertir chaque LessonVideoItem en LessonVideoItemRep
            List<LessonVideoItemRep> lessonVideoItemRepList = new ArrayList<>();
            for (LessonVideoItem videoItem : lesson.getVideo()) {
                LessonVideoItemRep lessonVideoItemRep = new LessonVideoItemRep();
                lessonVideoItemRep.setId(videoItem.getId());
                lessonVideoItemRep.setThumbnail(videoItem.getThumbnail());
                lessonVideoItemRep.setUrl(videoItem.getUrl());
                lessonVideoItemRep.setName(videoItem.getName());

                lessonVideoItemRepList.add(lessonVideoItemRep);
            }

            lessonDTO.setVideo(lessonVideoItemRepList);
            lessonDTOList.add(lessonDTO);
        }
        return lessonDTOList;
    }
   @Override
    public List<LessonDTO> getLessonsByCourId(Integer courId) {
        List<Lesson> lessons = lessonRepository.findByCourId(courId);
        List<LessonDTO> lessonDTOList = new ArrayList<>();

        for (Lesson lesson : lessons) {
            LessonDTO lessonDTO = new LessonDTO();
            lessonDTO.setId(lesson.getId());
            lessonDTO.setName(lesson.getName());
            lessonDTO.setDescription(lesson.getDescription());
            lessonDTO.setThumbnail(lesson.getThumbnail());

            if (lesson.getCour() != null) {
                lessonDTO.setCourId(lesson.getCour().getId());
            }

            // Convert each LessonVideoItem to LessonVideoItemRep
            List<LessonVideoItemRep> lessonVideoItemRepList = new ArrayList<>();
            for (LessonVideoItem videoItem : lesson.getVideo()) {
                LessonVideoItemRep lessonVideoItemRep = new LessonVideoItemRep();
                lessonVideoItemRep.setId(videoItem.getId());
                lessonVideoItemRep.setThumbnail(videoItem.getThumbnail());
                lessonVideoItemRep.setUrl(videoItem.getUrl());
                lessonVideoItemRep.setName(videoItem.getName());

                lessonVideoItemRepList.add(lessonVideoItemRep);
            }

            lessonDTO.setVideo(lessonVideoItemRepList);
            lessonDTOList.add(lessonDTO);
        }
        return lessonDTOList;
    }


    @Override
    public LessonDTO getLessonByName(String name) {
        Lesson lesson = lessonRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        return convertToLessonDTO(lesson);
    }


    // Convertit un Lesson en LessonDTO
    private LessonDTO convertToLessonDTO(Lesson lesson) {
        LessonDTO lessonDTO = new LessonDTO();
        lessonDTO.setId(lesson.getId());
        lessonDTO.setName(lesson.getName());
        if (lesson.getCour() != null) {
           // lessonDTO.setCourNom(lesson.getCour().getName());
            //lessonDTO.setCour(lesson.getCour());
            lessonDTO.setCourId(lesson.getCour().getId());
        }
        lessonDTO.setDescription(lesson.getDescription());
        lessonDTO.setThumbnail(lesson.getThumbnail());
        // ajoute les vidéos si nécessaire
        return lessonDTO;
    }


    @Override
    public ResponseEntity<String> deleteLesson(Integer id) {
        return null;
    }
}
