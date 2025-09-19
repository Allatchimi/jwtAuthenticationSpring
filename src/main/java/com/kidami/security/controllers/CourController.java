package com.kidami.security.controllers;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.mappers.CourMapper;
import com.kidami.security.models.Enrollment;
import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.CourService;
import com.kidami.security.utils.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cours")
public class CourController {

    private final CourService courService;
    private final CourMapper courMapper;

    public CourController(CourService courService, CourMapper courMapper) {
        this.courService = courService;
        this.courMapper = courMapper;
    }

    @Operation(summary = "Uploader cour avec un fichier", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping(value = "/creatCour", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<CourDTO>> saveCour(
            @Parameter(
                    description = "Données du cours",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourSaveDTO.class))
            )
            @Valid @RequestPart("courSaveDTO") CourSaveDTO courSaveDTO,
            @Parameter(description = "Fichier thumbnail du cours",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
            @RequestPart("file") MultipartFile file,
            Authentication authentication){
        String teacherName = authentication.getName();
        log.info("user added teacher name: {}", teacherName);
        CourDTO courDTO = courService.addCour(courSaveDTO,teacherName ,file);
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
    ResponseEntity<ApiResponse<CourDeteailDTO>> getCour(@PathVariable(value="id") Long courId) {
        CourDeteailDTO cour = courService.courtDetails(courId);
        return ResponseEntity.ok(ResponseUtil.success("Course retrieved successfully",cour,null));
    }

    @PutMapping("/updateCour")
    public  ResponseEntity<ApiResponse<CourDTO>> updateCour(@RequestBody CourUpdateDTO courUpdateDTO){
        CourDTO courDTO = courService.updateCour(courUpdateDTO);
        return ResponseEntity.ok(ResponseUtil.success("Course updated successfully",courDTO,null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCour(@PathVariable("id") Long id) {
        boolean isDeleted = courService.deleteCour(id);
        if (isDeleted) {
            return ResponseEntity.ok(ResponseUtil.success("Course deleted successfully", null, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("Course not found", null, null));
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<CourDTO>>> getPopularCourses() {
        List<CourDTO> courDTOS = courService.getPopularCourses();
        return ResponseEntity.ok(ResponseUtil.success("Popular courses retrieved successfully", courDTOS, null));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{courseId}/enroll")
    public ResponseEntity<ApiResponse<Enrollment>> enrollToCourse(@PathVariable Long courseId,
                                                     Authentication principal) {
        Enrollment enrollment = courService.enrollToCourse(courseId, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.success("Course enrolled successfully", enrollment, null));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/my-courses")
    public ResponseEntity<ApiResponse<List<EnrollementDTO>>> getMyCourses(Authentication principal) {
        List<EnrollementDTO> enrollementDTOS = courService.getUserCourses(principal.getName());
        return ResponseEntity.ok(ResponseUtil.success("My courses retrieved successfully", enrollementDTOS, null));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/teacher-courses")
    public ResponseEntity<ApiResponse<List<CourDTO>>> getTeacherCourse(Authentication principal) {
        List<CourDTO> courDTOS = courService.getTeacherCourses(principal.getName());
        return ResponseEntity.ok(ResponseUtil.success("Teacher courses retrieved successfully", courDTOS, null));
    }
    /*
    @GetMapping("/searchCour")
    public ResponseEntity<ApiResponse<List<CourDTO>>> searchCour(@RequestParam(name = "keyword",defaultValue = "") String keyword) {
        List<CourDTO> serchCour = courService.searchCour("%"+keyword+"%");
        return ResponseEntity.ok(ResponseUtil.success("Cour searched successfully", serchCour, null));
    }*/
    @GetMapping("/searchCour")
    public ResponseEntity<ApiResponse<Page<CourDTO>>> searchCour(
            @RequestParam(required = false) String kw,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer score,
            @RequestParam(required = false) String categoryName,
            @RequestParam(required = false) String teacherName,
            Pageable pageable
    ) {
        Page<CourDTO> serchCour =  courService.searchCour(kw, minPrice, maxPrice, score, categoryName, teacherName, pageable);
        return ResponseEntity.ok(ResponseUtil.success("Cour searched successfully", serchCour, null));
    }


    @GetMapping("/top")
    public ResponseEntity<ApiResponse<Page<CourDTO>>> top(@RequestParam(defaultValue="0") int page,
                               @RequestParam(defaultValue="10") int size){
            Page<CourDTO> courDTOPage = courService.getTopCourses(page,size);
        return ResponseEntity.ok(ResponseUtil.success("Top courses retrieved successfully", courDTOPage, null));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<Page<CourDTO>>> recent(@RequestParam(defaultValue="0") int page,
                                  @RequestParam(defaultValue="10") int size) {

        Page<CourDTO> courDTOPage = courService.getRecentCourses(page, size);
        return ResponseEntity.ok(ResponseUtil.success("Recent courses retrieved successfully", courDTOPage, null));
    }
    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> toggleFavorite(@PathVariable Long id, Authentication principal){
        Long userId = Long.valueOf(principal.getName()); // supposer userId est dans principal.name
        courService.toggleFavorite(userId, id);
        return ResponseEntity.ok().build();
    }

    /*
    @GetMapping("/teacher/{teacherId}")
    public Page<CourDTO> byTeacher(@PathVariable Long teacherId,
                                     @RequestParam(defaultValue="0") int page,
                                     @RequestParam(defaultValue="10") int size){
        return courService.getCourByTeacher(teacherId, page, size).map(CourMapper::toDto);
    }*/

        /*
    @PostMapping("/{id}/purchase")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PurchaseDTO> purchase(@PathVariable Long id, Authentication principal){
        Long userId = Long.valueOf(principal.getName());
        PurchaseDTO p = courService.initiatePurchase(userId, id, "USD");
        // appeler service paiement: retourner client secret / checkout url
        return ResponseEntity.ok(new InitiatePaymentResponse(p.getId(), "checkout_url_placeholder"));
    }
*/

}