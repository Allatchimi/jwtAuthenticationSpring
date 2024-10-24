package com.kidami.security.controllers;

import com.kidami.security.requests.LessonVideoItemReq;
import com.kidami.security.responses.LessonVideoItemRep;
import com.kidami.security.services.LessonVideoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/LessonVideoItem")
public class LessonVideoItemController {
    @Autowired
    private LessonVideoItemService lessonVideoItemService;

    @PostMapping("/add")
    public ResponseEntity<LessonVideoItemRep> addLessonVideoItem(@RequestBody LessonVideoItemReq lessonVideoItemReq) {
        LessonVideoItemRep response = lessonVideoItemService.addLessonVideoItem(lessonVideoItemReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Retourne l'objet avec un statut 201
    }

    @PutMapping("/update")
    public ResponseEntity<LessonVideoItemRep> updateLessonVideoItem(@RequestBody LessonVideoItemReq lessonVideoItemReq) {
        LessonVideoItemRep response = lessonVideoItemService.updateLessonVideoItem(lessonVideoItemReq);
        return ResponseEntity.ok(response); // Retourne l'objet mis à jour avec un statut 200
    }

    // Méthode pour récupérer tous les cours vidéo
    @GetMapping("/all")
    public ResponseEntity<List<LessonVideoItemRep>> getAllLessonVideoItems() {
        List<LessonVideoItemRep> lessonVideoItem = lessonVideoItemService.getAllLessonVideoItem();
        return ResponseEntity.ok(lessonVideoItem);
    }

    // Méthode pour supprimer un cours par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLessonVideoItem(@PathVariable Integer id) {
        boolean isDeleted = lessonVideoItemService.deleteLessonVideoItem(id).hasBody();
        if (isDeleted) {
            return ResponseEntity.ok("lessonVideoItem supprimé avec succès");
        } else {
            return ResponseEntity.status(404).body("lessonVideoItem non trouvé");
        }
    }

}