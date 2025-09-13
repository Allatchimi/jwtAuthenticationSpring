package com.kidami.security.services.impl;

import com.kidami.security.dto.courDTO.CourDTO;
import com.kidami.security.dto.courDTO.CourDeteailDTO;
import com.kidami.security.dto.courDTO.CourSaveDTO;
import com.kidami.security.dto.courDTO.CourUpdateDTO;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.CourMapper;
import com.kidami.security.models.*;
import com.kidami.security.repository.CategoryRepository;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.repository.EnrollmentRepository;
import com.kidami.security.repository.UserRepository;
import com.kidami.security.services.CourService;
import com.kidami.security.services.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CourServiceImpl implements CourService {

    private static final Logger logger = LoggerFactory.getLogger(CourServiceImpl.class);
    private final UserRepository userRepository;
    private final CourRepository courRepository;
    private final CategoryRepository categoryRepository;
    private final CourMapper courMapper;
    private final EnrollmentRepository enrollmentRepository;
    private final StorageService storageService;


    public CourServiceImpl(CourMapper courMapper, CourRepository courRepository, CategoryRepository categoryRepository, EnrollmentRepository enrollmentRepository, UserRepository userRepository , StorageService storageService) {
        this.courMapper = courMapper;
        this.courRepository = courRepository;
        this.categoryRepository = categoryRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
    }

    @Override
    public CourDTO addCour(CourSaveDTO courSaveDTO, String teacherUsername, MultipartFile file) {
        logger.debug("Tentative de création d'un cours: {} par {}", courSaveDTO.getName(), teacherUsername);

        User teacher = userRepository.findByEmail(teacherUsername)
                .orElseThrow(() -> new RuntimeException("Teacher non Trouvé"));

        validateCourSaveDTO(courSaveDTO);

        if (courRepository.existsByName(courSaveDTO.getName())) {
            logger.warn("Tentative de création d'un cours en double: {}", courSaveDTO.getName());
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
            logger.info("Cours créé avec succès: {} (par : {})", savedCour.getName(), savedCour.getTeacher().getName());
            return courMapper.toDTO(savedCour);

        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (DataAccessException e) {
            logger.error("Erreur d'accès aux données lors de la création du cours: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur technique lors de la création du cours", e);
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création du cours: {}", e.getMessage(), e);
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
        logger.debug("Tentative de récupération de tous les cours");
        List<Cour> cours = courRepository.findAll();
        logger.info("{} cours récupérés avec succès", cours.size());
        return cours.stream()
                .map(courMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourDTO updateCour(CourUpdateDTO courUpdateDTO) {

        logger.debug("Mise à jour du cours ID: {}", courUpdateDTO.getId());
        // Vérifier si le cours existe
        Cour cour = courRepository.findById(courUpdateDTO.getId())
                .orElseThrow(() ->{
                    logger.warn("le cours n existe pas: {}", courUpdateDTO.getName());
                    return  new ResourceNotFoundException("Course", "id", courUpdateDTO.getId());
                });

        // Vérifier si le nouveau nom existe déjà (pour un autre cours)
        if (courUpdateDTO.getName() != null &&
                !cour.getName().equals(courUpdateDTO.getName()) &&
                courRepository.existsByNameAndIdNot(courUpdateDTO.getName(), courUpdateDTO.getId())) {
            logger.warn("le nouveau nom existe déjà pour un autre cours: {}", cour.getName());
            throw new DuplicateResourceException("Course", "name", courUpdateDTO.getName());
        }

        logger.trace("Données de mise à jour valides: {}", courUpdateDTO);
        try {
            // Mettre à jour les champs
            if (courUpdateDTO.getName() != null) cour.setName(courUpdateDTO.getName());
            if (courUpdateDTO.getDescription() != null) cour.setDescription(courUpdateDTO.getDescription());
            if (courUpdateDTO.getThumbnail() != null) cour.setThumbnail(courUpdateDTO.getThumbnail());
            if (courUpdateDTO.getPrice() != null) cour.setPrice(courUpdateDTO.getPrice());
            if (courUpdateDTO.getAmountTotal() != null) cour.setAmountTotal(courUpdateDTO.getAmountTotal());
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
                            logger.warn("Categorie n existe pas: {}", courUpdateDTO.getCategorieId());
                           return  new ResourceNotFoundException("Category", "id", courUpdateDTO.getCategorieId());
                        });
                cour.setCategorie(categorie);
            }

            Cour updatedCour = courRepository.save(cour);
            logger.info("le cour a ete bien mise a jour : {}", updatedCour);
            return courMapper.toDTO(updatedCour);

        }catch (Exception e) {
            logger.error("Erreur lors de la mise a jour du cour: {}", e.getMessage());
            throw e;

        }
    }

    @Override
    public boolean deleteCour(Long id) {
        logger.debug("Tentative de suppression du cours ID: {}", id);
        // Vérifier si le cours existe
        if (!courRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un cours inexistant ID: {}", id);
            throw new ResourceNotFoundException("Course", "id", id);
        }
        try {
            courRepository.deleteById(id);
            logger.info("Cours supprimé avec succès ID: {}", id);
            return true;
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression du cours ID: {} - {}", id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du cours", e);
        }
    }

    @Override
    public CourDeteailDTO courtDetails(Integer courId) {
        logger.debug("Tentative de récupération du cours avec ID: {}", courId);

        try {
            Cour cour = courRepository.findById(courId)
                    .orElseThrow(() -> {
                        logger.warn("Cours non trouvé avec ID: {}", courId);
                        return new ResourceNotFoundException("Course", "id", courId);
                    });

            logger.info("Cours récupéré avec succès: {}", cour.getName());
            return courMapper.toDetailDTO(cour);

        } catch (ResourceNotFoundException e) {
            throw e;
        }
    }

    @Override
    public List<Cour> getPopularCourses() {
        return courRepository.findTop10ByOrderByEnrollmentCountDesc();
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
        enrollment.setEnrollmentDate(LocalDateTime.now());
        enrollment.setPaymentStatus(PaymentStatus.PENDING);
        enrollment.setAmountPaid(course.getPrice());

        return enrollmentRepository.save(enrollment);
    }
    @Override
    public List<Cour> getUserCourses(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return enrollmentRepository.findByStudent(user)
                .stream()
                .map(Enrollment::getCour)
                .collect(Collectors.toList());
    }
    @Override
    public List<Cour> getTeacherCourses(String username) {
        User teacher = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        return courRepository.findByTeacher(teacher);
    }

}