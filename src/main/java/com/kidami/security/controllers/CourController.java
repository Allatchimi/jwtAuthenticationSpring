package com.kidami.security.controllers;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.CourService;
import com.kidami.security.utils.ResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
@RestController
@RequestMapping("/api/cours")
public class CourController {

    private final CourService courService;

    public CourController(CourService courService) {
        this.courService = courService;
    }

    @PostMapping("/creatCour")
    public ResponseEntity<ApiResponse<CourDTO>> saveCour(@Valid @RequestBody CourSaveDTO courSaveDTO){
        CourDTO courDTO =courService.addCour(courSaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success("Course added successfully",courDTO,null));
    }

    @GetMapping("/getAllCours")
    public ResponseEntity<ApiResponse<List<CourDTO>>> getAllCours() {
        List<CourDTO> allCours = courService.getAllCours();
        if (allCours.isEmpty()) {
            return ResponseEntity.ok()
                    .body(ResponseUtil.success("No courses found", Collections.emptyList(), null));
        }
        return ResponseEntity.ok()
                .body(ResponseUtil.success("Courses retrieved successfully", allCours, null));
    }

    @GetMapping("/courtDetails/{id}")
    ResponseEntity<ApiResponse<CourDeteailDTO>> getCour(@PathVariable(value="id") Integer courId) {
        CourDeteailDTO cour = courService.courtDetails(courId);
        return ResponseEntity.ok(ResponseUtil.success("Course retrieved successfully",cour,null));
    }

    @PutMapping("/updateCour")
    public  ResponseEntity<ApiResponse<CourDTO>> updateCour(@RequestBody CourUpdateDTO courUpdateDTO){
        CourDTO courDTO = courService.updateCour(courUpdateDTO);
        return ResponseEntity.ok(ResponseUtil.success("Course updated successfully",courDTO,null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCour(@PathVariable("id") Integer id) {
        boolean isDeleted = courService.deleteCour(id);
        if (isDeleted) {
            return ResponseEntity.ok(ResponseUtil.success("Course deleted successfully", null, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("Course not found", null, null));
        }
    }
}
