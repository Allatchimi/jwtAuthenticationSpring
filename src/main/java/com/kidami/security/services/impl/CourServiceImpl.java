package com.kidami.security.services.impl;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.dto.enrollementDTO.EnrollementDTO;
import com.kidami.security.dto.purchaseDTO.PurchaseDTO;
import com.kidami.security.enums.PurchaseStatus;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.CourMapper;
import com.kidami.security.mappers.EnrollementMapper;
import com.kidami.security.mappers.PurchaseMapper;
import com.kidami.security.models.*;
import com.kidami.security.repository.*;
import com.kidami.security.services.CourService;
import com.kidami.security.services.StorageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CourServiceImpl implements CourService {

    private static final Logger log = LoggerFactory.getLogger(CourServiceImpl.class);
    private final UserRepository userRepository;
    private final CourRepository courRepository;
    private final CategoryRepository categoryRepository;
    private final CourMapper courMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final StorageService storageService;
    private final EnrollementMapper enrollementMapper;
    private final FavoriteRepository favoriteRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;


    public CourServiceImpl(CourMapper courMapper, CourRepository courRepository, CategoryRepository categoryRepository,
                           EnrollmentRepository enrollmentRepository,
                           UserRepository userRepository , StorageService storageService,
                           EnrollementMapper enrollementMapper, FavoriteRepository favoriteRepository,
                           PurchaseRepository purchaseRepository, PurchaseMapper purchaseMapper) {
        this.courMapper = courMapper;
        this.courRepository = courRepository;
        this.categoryRepository = categoryRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.enrollementMapper = enrollementMapper;
        this.favoriteRepository = favoriteRepository;
        this.purchaseRepository = purchaseRepository;
        this.purchaseMapper = purchaseMapper;
    }

    @Override
    public CourDTO addCour(CourSaveDTO courSaveDTO, String teacherUsername, MultipartFile file) {
        log.debug("Tentative de création d'un cours: {} par {}", courSaveDTO.getName(), teacherUsername);
        User teacher = userRepository.findByEmail(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher non Trouvé"));

        validateCourSaveDTO(courSaveDTO);

        if (courRepository.existsByName(courSaveDTO.getName())) {
            log.warn("Tentative de création d'un cours en double: {}", courSaveDTO.getName());
            throw new DuplicateResourceException("Course", "name", courSaveDTO.getName());
        }
        try {
            Category categorie = categoryRepository.findById(courSaveDTO.getCategorieId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", courSaveDTO.getCategorieId())
                    );

            Cour cour = courMapper.createCourFromDTO(courSaveDTO, categorie);
            cour.setTeacher(teacher);
            // Gestion du fichier
            if (file != null && !file.isEmpty()) {
                String imageName = storageService.saveImage(file, "cours");
               String thumbnailUrl = "api/"+imageName;
                cour.setThumbnail(thumbnailUrl);
            }
            Cour savedCour = courRepository.save(cour);
            log.info("Cours créé avec succès: {} (par : {})", savedCour.getName(), savedCour.getTeacher().getName());
            return courMapper.toDTO(savedCour);

        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Erreur d'accès aux données lors de la création du cours: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur technique lors de la création du cours", e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du cours: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création du cours", e);
        }
    }

    private void validateCourSaveDTO(CourSaveDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du cours est obligatoire");
        }
        if (dto.getCategorieId() == null) {
            throw new IllegalArgumentException("L'ID de la catégorie est obligatoire");
        }
    }

    @Override
    public List<CourDTO> getAllCours() {
        log.debug("Tentative de récupération de tous les cours");
        List<Cour> cours = courRepository.findAll();
        log.info("{} cours récupérés avec succès, tout les cours", cours.size());
        return cours.stream()
                .map(courMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourDTO updateCour(CourUpdateDTO courUpdateDTO) {

        log.debug("Mise à jour du cours ID: {}", courUpdateDTO.getId());
        // Vérifier si le cours existe
        Cour cour = courRepository.findById(courUpdateDTO.getId())
                .orElseThrow(() ->{
                    log.warn("le cours n existe pas: {}", courUpdateDTO.getName());
                    return  new ResourceNotFoundException("Course", "id", courUpdateDTO.getId());
                });

        // Vérifier si le nouveau nom existe déjà (pour un autre cours)
        if (courUpdateDTO.getName() != null &&
                !cour.getName().equals(courUpdateDTO.getName()) &&
                courRepository.existsByNameAndIdNot(courUpdateDTO.getName(), courUpdateDTO.getCategorieId())) {
            log.warn("le nouveau nom existe déjà pour un autre cours: {}", cour.getName());
            throw new DuplicateResourceException("Course", "name", courUpdateDTO.getName());
        }

        log.trace("Données de mise à jour valides: {}", courUpdateDTO);
        try {
            // Mettre à jour les champs
            if (courUpdateDTO.getName() != null) cour.setName(courUpdateDTO.getName());
            if (courUpdateDTO.getDescription() != null) cour.setDescription(courUpdateDTO.getDescription());
            if (courUpdateDTO.getThumbnail() != null) cour.setThumbnail(courUpdateDTO.getThumbnail());
            if (courUpdateDTO.getPrice() != null) cour.setPrice(courUpdateDTO.getPrice());
           // if (courUpdateDTO.getAmountTotal() != null) cour.setAmountTotal(courUpdateDTO.getAmountTotal());
            if (courUpdateDTO.getLessonNum() != null) cour.setLessonNum(courUpdateDTO.getLessonNum());
            if (courUpdateDTO.getVideoLen() != null) cour.setVideoLen(courUpdateDTO.getVideoLen());
            if (courUpdateDTO.getFollow() != null) cour.setFollow(courUpdateDTO.getFollow());
            if (courUpdateDTO.getDownNum() != null) cour.setDownNum(courUpdateDTO.getDownNum());
            if (courUpdateDTO.getScore() != null) cour.setScore(courUpdateDTO.getScore());
            if (courUpdateDTO.getUserToken() != null) cour.setUserToken(courUpdateDTO.getUserToken());

            // Mettre à jour la catégorie si nécessaire
            if (courUpdateDTO.getCategorieId() != null) {
                Category categorie = categoryRepository.findById(courUpdateDTO.getCategorieId())
                        .orElseThrow(() -> {
                            log.warn("Categorie n existe pas: {}", courUpdateDTO.getCategorieId());
                           return  new ResourceNotFoundException("Category", "id", courUpdateDTO.getCategorieId());
                        });
                cour.setCategorie(categorie);
            }
            cour.setUpdatedAt(Instant.now());

            Cour updatedCour = courRepository.save(cour);
            log.info("le cour a ete bien mise a jour : {}", updatedCour);
            return courMapper.toDTO(updatedCour);

        }catch (Exception e) {
            log.error("Erreur lors de la mise a jour du cour: {}", e.getMessage());
            throw e;

        }
    }

    @Override
    public boolean deleteCour(Long id) {
        log.debug("Tentative de suppression du cours ID: {}", id);
        // Vérifier si le cours existe
        if (!courRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un cours inexistant ID: {}", id);
            throw new ResourceNotFoundException("Course", "id", id);
        }
        try {
            courRepository.deleteById(id);
            log.info("Cours supprimé avec succès ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du cours ID: {} - {}", id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du cours", e);
        }
    }

    @Override
    public CourDeteailDTO courtDetails(Long courId) {
        log.debug("Tentative de récupération du cours avec ID: {}", courId);
        Cour cour = courRepository.findById(courId)
                .orElseThrow(() -> {
                    log.warn("Cours non trouvé avec ID: {}", courId);
                    return new ResourceNotFoundException("Course", "id", courId);
                });
        log.info("Cours récupéré avec succès: {}", cour.getName());
        return courMapper.toDetailDTO(cour);
    }

    @Override
    public List<CourDTO> getPopularCourses() {
        log.debug("Tentative de la liste des courses populaire");
         List<Cour> cours = courRepository.findTop10ByOrderByEnrollmentCountDesc();
        log.info("{} cours récupérés avec succès", cours.size());
        return cours.stream()
                .map(courMapper::toDTO)
                .collect(Collectors.toList()
                );
    }

    @Override
    public Enrollment enrollToCourse(Long courseId, String username) {
        User student = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Cour course = courRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        // Vérifier si déjà inscrit
        if (enrollmentRepository.existsByStudentAndCour(student, course)) {
            throw new RuntimeException("Already enrolled in this course");
        }
        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCour(course);
        enrollment.setEnrolledAt(LocalDateTime.now());


        return enrollmentRepository.save(enrollment);
    }

    @Override
    public List<EnrollementDTO> getUserCourses(String username) {
        log.debug("Tentative de reuperation  de la liste des courses de: {}", username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(user.getId());
        log.info("les nombre de cours suivie par {} est : {}", user.getName(),enrollments.size());
        return enrollments.stream()
                .map(enrollementMapper::toDTO)
                .collect(Collectors.toList());

    }
    @Override
    public List<CourDTO> getTeacherCourses(String username) {
        log.debug("Tentative de la liste des courses de: {}", username);
        User teacher = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        List<Cour> cours =  courRepository.findByTeacher(teacher);
        log.info("les nombres de {} est : {}", username, cours.size());
        return cours.stream().map(courMapper::toDTO).collect(Collectors.toList()) ;
    }
    /*
    @Override
    public List<CourDTO> searchCour(String keyword) {
        List<Cour> cours =  courRepository.searchCour(keyword);
        return cours.stream()
                .map(courMapper::toDTO)
                .collect(Collectors.toList());

    }*/
    @Override
    public Page<CourDTO> searchCour(String kw, Double minPrice, Double maxPrice,
                                    Integer score, String categoryName, String teacherName, Pageable pageable) {

        Specification<Cour> spec = Specification
                .where(CourSpecification.hasKeyword(kw))
                .and(CourSpecification.hasPriceBetween(minPrice, maxPrice))
                .and(CourSpecification.hasScore(score))
                .and(CourSpecification.hasCategory(categoryName))
                .and(CourSpecification.hasTeacher(teacherName));
        Page<Cour> cours = courRepository.findAll(spec, pageable);

        return cours.map(courMapper::toDTO);

    }


    public Page<Cour> getTopCourses(int page, int size){
        return courRepository.findTopCourses(PageRequest.of(page, size));
    }

    public Page<Cour> getRecentCourses(int page, int size){
        return courRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page,size));
    }

    @Transactional
    public void toggleFavorite(Long userId, Long courseId){
        var existing = favoriteRepository.findByUserIdAndCourseId(userId, courseId);
        if(existing.isPresent()){
            favoriteRepository.delete(existing.get());
        } else {
            var fav = new Favorite();
            fav.setUser(userRepository.findById(userId).orElseThrow());
            fav.setCourse(courRepository.findById(courseId).orElseThrow());
            favoriteRepository.save(fav);
        }
    }

    public List<Cour> getFavorites(Long userId, int page, int size){
        return favoriteRepository.findByUserId(userId, PageRequest.of(page,size))
                .stream().map(Favorite::getCourse).collect(Collectors.toList());
    }

    @Transactional
    public PurchaseDTO initiatePurchase(Long userId, Long courseId, String currency){
        User user = userRepository.findById(userId).orElseThrow();
        Cour course = courRepository.findById(courseId).orElseThrow();

        Purchase p = new Purchase();
        p.setBuyer(user);
        p.setCourses(Set.of(course)); // ✅ ici la correction
        p.setAmountTotal(course.getPrice());
        p.setCurrency(currency);
        p.setStatus(PurchaseStatus.PENDING);

        Purchase purchase = purchaseRepository.save(p);

        return purchaseMapper.toDTO(purchase); // vérifie bien que c'est toDTO() et pas toDto()
    }

}