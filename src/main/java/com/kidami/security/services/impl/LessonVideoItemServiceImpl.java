package com.kidami.security.services.impl;

import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.models.Lesson;
import com.kidami.security.models.LessonVideoItem;
import com.kidami.security.repository.LessonRepository;
import com.kidami.security.repository.LessonVideoItemRepository;
import com.kidami.security.requests.LessonVideoItemReq;
import com.kidami.security.responses.LessonVideoItemRep;
import com.kidami.security.services.LessonVideoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonVideoItemServiceImpl implements LessonVideoItemService {

    @Autowired
    private LessonVideoItemRepository lessonVideoItemRepository;
    @Autowired
    private LessonRepository lessonRepository;

    /*
    @Override
    public LessonVideoItemRep addLessonVideoItem(LessonVideoItemReq lessonVideoItemReq) {
        // Rechercher la leçon existante par son ID
        Lesson lesson = lessonRepository.findById(lessonVideoItemReq.getLessonId())
                .orElseThrow(() -> new RuntimeException("Leçon introuvable pour l'ID : " + lessonVideoItemReq.getLessonId()));
        // ... (le reste de votre logique)
    }*/

    @Override
    public LessonVideoItemRep addLessonVideoItem(Integer lessonId, LessonVideoItemReq lessonVideoItemReq) {
        // Rechercher la leçon existante par son ID
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Leçon introuvable pour l'ID : " + lessonId));

        // Créer et remplir un nouvel objet LessonVideoItem
        LessonVideoItem lessonVideoItem = new LessonVideoItem();
        lessonVideoItem.setName(lessonVideoItemReq.getName());
        lessonVideoItem.setThumbnail(lessonVideoItemReq.getThumbnail());
        lessonVideoItem.setUrl(lessonVideoItemReq.getUrl());

        // Associer la leçon existante à l'élément vidéo
        lessonVideoItem.setLesson(lesson);

        // Ajouter cet item à la liste des vidéos de la leçon
        lesson.getVideo().add(lessonVideoItem);

        // Sauvegarder l'item vidéo et mettre à jour la leçon dans la base de données
        lessonRepository.save(lesson);

        // Préparer et retourner le DTO de réponse
        LessonVideoItemRep response = new LessonVideoItemRep();
        response.setId(lessonVideoItem.getId());
        response.setName(lessonVideoItem.getName());
        response.setUrl(lessonVideoItem.getUrl());
        response.setThumbnail(lessonVideoItem.getThumbnail());

        return response;
    }


    @Override
    public List<LessonVideoItemRep> getAllLessonVideoItem() {

        // Récupérez tous les cours de la base de données
        List<LessonVideoItem> lessonVideoItems = lessonVideoItemRepository.findAll();

        // Mappez chaque cours à un CourResponse
        return lessonVideoItems.stream().map(lessonVideoItem -> {
            LessonVideoItemRep response = new LessonVideoItemRep();
            response.setId(lessonVideoItem.getId());
            response.setUrl(lessonVideoItem.getUrl());
            response.setName(lessonVideoItem.getName());
            response.setThumbnail(lessonVideoItem.getThumbnail());
            // Récupérez l'ID et le nom de la leçon associée
            if (lessonVideoItem.getLesson() != null) {
                response.setLesson_id(lessonVideoItem.getLesson().getId());
                response.setLessonName(lessonVideoItem.getLesson().getName());
            }
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public LessonVideoItemRep updateLessonVideoItem(LessonVideoItemReq lessonVideoItemReq) {
        // Implémentez la logique pour mettre à jour un cours ici
        LessonVideoItem lessonVideoItem = lessonVideoItemRepository.findById(lessonVideoItemReq.getId())
                .orElseThrow(() -> new ResourceNotFoundException("LessonVideoItem non trouvé avec ID : " + lessonVideoItemReq.getId()));

        lessonVideoItem.setName(lessonVideoItemReq.getName());
        lessonVideoItem.setUrl(lessonVideoItemReq.getUrl());
        lessonVideoItem.setThumbnail(lessonVideoItemReq.getThumbnail());

        // Mettez à jour d'autres propriétés selon vos besoins
        lessonVideoItem = lessonVideoItemRepository.save(lessonVideoItem);

        // Créez et retournez la réponse
        LessonVideoItemRep response = new LessonVideoItemRep();
        response.setName(lessonVideoItem.getName());
        response.setUrl(lessonVideoItem.getUrl());
        response.setThumbnail(lessonVideoItemReq.getThumbnail());

        return response;
    }
    @Override
    public ResponseEntity<String> deleteLessonVideoItem(Integer id) {
        // Vérifiez si le cours existe
        if (!lessonVideoItemRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("lessonVideoItem non trouvé avec ID : " + id);
        }
        // Supprimez le cours
        lessonVideoItemRepository.deleteById(id);
        return ResponseEntity.ok("lessonVideoItem supprimé avec succès !");
    }

    @Override
    public List<LessonVideoItemRep> getLessonsByLessonId(Integer lesson_id) {
        List<LessonVideoItem> lessonVideoItems = lessonVideoItemRepository.findByLessonId(lesson_id);

        List<LessonVideoItemRep> lessonVideoItemRepList = new ArrayList<>();
         for( LessonVideoItem lessonVideoItem : lessonVideoItems ){
             LessonVideoItemRep lessonVideoItemRep = new LessonVideoItemRep();
             lessonVideoItemRep.setId(lessonVideoItem.getId());
             lessonVideoItemRep.setName(lessonVideoItem.getName());
             lessonVideoItemRep.setUrl(lessonVideoItem.getUrl());
             lessonVideoItemRep.setThumbnail(lessonVideoItem.getThumbnail());
             lessonVideoItemRep.setLesson_id(lessonVideoItem.getLesson().getId());
             lessonVideoItemRep.setLessonName(lessonVideoItem.getLesson().getName());

             lessonVideoItemRepList.add(lessonVideoItemRep);
         }
        return lessonVideoItemRepList;
    }
}
