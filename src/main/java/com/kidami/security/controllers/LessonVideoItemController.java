package com.kidami.security.controllers;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemSaveDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.responses.LessonVideoItemRep;
import com.kidami.security.services.LessonVideoItemService;
import com.kidami.security.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/LessonVideoItem")
public class LessonVideoItemController {
    private final LessonVideoItemService lessonVideoItemService;

    public LessonVideoItemController(LessonVideoItemService lessonVideoItemService) {
        this.lessonVideoItemService = lessonVideoItemService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<LessonVideoItemDTO>> addLessonVideoItem(
            @RequestParam Integer lessonId,
            @RequestBody LessonVideoItemSaveDTO lessonVideoItemSaveDTO) {
        LessonVideoItemDTO response = lessonVideoItemService.addLessonVideoItem(lessonId, lessonVideoItemSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.created("Video succes added",response,null)); // Retourne l'objet avec un statut 201
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<LessonVideoItemDTO>> updateLessonVideoItem(@RequestBody LessonVideoItemUpdateDTO lessonVideoItemUpdateDTO) {
        LessonVideoItemDTO response = lessonVideoItemService.updateLessonVideoItem(lessonVideoItemUpdateDTO);
        return ResponseEntity.ok(ResponseUtil.success("Video updated succefully",response,null)); // Retourne l'objet mis à jour avec un statut 200
    }

    // Méthode pour récupérer tous les cours vidéo
    @GetMapping("/allVideoItems")
    public ResponseEntity<ApiResponse<List<LessonVideoItemDTO>>> getAllLessonVideoItems() {
        List<LessonVideoItemDTO> lessonVideoItem = lessonVideoItemService.getAllLessonVideoItem();
        return ResponseEntity.ok(ResponseUtil.success("retrived all video succefully",lessonVideoItem,null));
    }

    @GetMapping("/byLesson/{lessonId}")
    public  ResponseEntity<ApiResponse<List<LessonVideoItemDTO>>> getLessonVideoItemByLessonId(@PathVariable Integer lessonId){
    List<LessonVideoItemDTO> lessonVideoItemRepList = lessonVideoItemService.getVideoItemByLessonId(lessonId);
    return ResponseEntity.ok(ResponseUtil.success("Retrived video succefully",lessonVideoItemRepList,null));
    }

    // Méthode pour supprimer un cours par son ID
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLessonVideoItem(@PathVariable Integer id) {
        boolean isDeleted = lessonVideoItemService.deleteLessonVideoItem(id);
        if (isDeleted) {
            return ResponseEntity.ok(ResponseUtil.success("lessonVideoItem supprimé avec succès",null,null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseUtil.error("lessonVideoItem non trouvé",null,null));
        }
    }

}