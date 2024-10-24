package com.kidami.security.services.impl;

import com.kidami.security.dto.CourDTO;
import com.kidami.security.dto.CourSaveDTO;
import com.kidami.security.dto.CourUpdateDTO;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.models.LessonVideoItem;
import com.kidami.security.repository.LessonVideoItemRepository;
import com.kidami.security.requests.LessonVideoItemReq;
import com.kidami.security.responses.LessonVideoItemRep;
import com.kidami.security.services.LessonVideoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonVideoItemServiceImpl implements LessonVideoItemService {

    @Autowired
    private LessonVideoItemRepository lessonVideoItemRepository;

    @Override
    public LessonVideoItemRep addLessonVideoItem(LessonVideoItemReq lessonVideoItemReq) {

        LessonVideoItem lessonVideoItem = new LessonVideoItem();
        lessonVideoItem.setName(lessonVideoItemReq.getName());
        lessonVideoItem.setThumbnail(lessonVideoItemReq.getThumbnail());
        lessonVideoItem.setUrl(lessonVideoItemReq.getUrl());

        lessonVideoItem = lessonVideoItemRepository.save(lessonVideoItem);

        // Logique pour ajouter l'item
        LessonVideoItemRep response = new LessonVideoItemRep();
        // Remplir response avec les données
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
}
