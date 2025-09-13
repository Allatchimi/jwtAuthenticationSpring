package com.kidami.security.services.impl;

import com.kidami.security.dto.lessonDTO.LessonDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import com.kidami.security.mappers.LessonMapper;

import com.kidami.security.dto.lessonDTO.LessonDelete;
import com.kidami.security.dto.lessonDTO.LessonSaveDTO;
import com.kidami.security.dto.lessonDTO.LessonUpdateDTO;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.LessonVideoItemMapper;
import com.kidami.security.models.Cour;
import com.kidami.security.models.Lesson;
import com.kidami.security.models.LessonVideoItem;
import com.kidami.security.repository.CourRepository;
import com.kidami.security.repository.LessonRepository;
import com.kidami.security.repository.LessonVideoItemRepository;
import com.kidami.security.services.LessonService;
import com.kidami.security.services.StorageService;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.stream.Collectors;
@Service
public class LessonServiceImpl implements LessonService {

    private static final Logger log = LoggerFactory.getLogger(LessonServiceImpl.class.getName());
    private final LessonRepository lessonRepository;
    private final CourRepository courRepository;
    private final LessonMapper lessonMapper;
    private final LessonVideoItemMapper lessonVideoItemMapper;
    private final LessonVideoItemRepository lessonVideoItemRepository;
    private final StorageService storageService;

    public LessonServiceImpl(
            LessonRepository lessonRepository ,
            CourRepository courRepository,
            LessonMapper lessonMapper,
            LessonVideoItemMapper lessonVideoItemMapper,
            LessonVideoItemRepository lessonVideoItemRepository,
            StorageService storageService) {
        this.lessonRepository = lessonRepository;
        this.courRepository = courRepository;
        this.lessonMapper = lessonMapper;
        this.lessonVideoItemMapper = lessonVideoItemMapper;
        this.lessonVideoItemRepository = lessonVideoItemRepository;
        this.storageService = storageService;
    }

    @Override
    public LessonDTO addLesson(LessonSaveDTO lessonSaveDTO, MultipartFile imageFile) {
        log.debug("Tantative de  créer  d un  lesson {}", lessonSaveDTO.getName());
        validateLessonSaveDTO(lessonSaveDTO);
        if(lessonRepository.existsByName(lessonSaveDTO.getName())) {
            log.warn("Tentative de création d'un Leçon en double: {}", lessonSaveDTO.getName());
            throw new DuplicateResourceException("Lesson", "name", lessonSaveDTO.getName());
        }
        try {
            Cour cour = courRepository.findById(lessonSaveDTO.getCourId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cour","id",lessonSaveDTO.getCourId()));
            // Convertir DTO -> Entity avec MapStruct
            Lesson lesson = lessonMapper.fromSaveDTO(lessonSaveDTO);
            lesson.setCour(cour);
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageName  = storageService.saveImage(imageFile, "lessons");
                String thumbnailUrl = "api/"+imageName;
                lesson.setThumbnail(thumbnailUrl);
            }
            List<LessonVideoItem> videoItems = lessonSaveDTO.getVideo().stream()
                    .map(videoSaveDTO -> {
                        LessonVideoItem videoItem = lessonVideoItemMapper.fromSaveDTO(videoSaveDTO);
                        videoItem.setLesson(lesson);
                        return videoItem;
                    })
                    .collect(Collectors.toList());

            lesson.setVideos(videoItems);
            // Sauvegarder
            Lesson savedLesson = lessonRepository.save(lesson);
            // Convertir Entity -> DTO avec MapStruct
            log.info("Leçon créé avec succès: {} (ID: {})", savedLesson.getName(), savedLesson.getId());
            return lessonMapper.toDTO(savedLesson);

        } catch (ResourceNotFoundException | DuplicateResourceException e) {
            throw e;
        } catch (DataAccessException e) {
            log.error("Erreur d'accès aux données lors de la création du lecçon: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur technique lors de la création du lecçon", e);
        } catch (Exception e) {
            log.error("Erreur inattendue lors de la création du lecçon: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création du lecçon", e);
        }

    }

    private void validateLessonSaveDTO(LessonSaveDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du Leçon est obligatoire");
        }
        if (dto.getCourId() == null) {
            throw new IllegalArgumentException("L'ID de la cour est obligatoire");
        }
    }

    @Override
    @Transactional
    public LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO) {
        log.debug("Mise à jour du Leçon ID: {}", lessonUpdateDTO.getId());

        Lesson lesson = lessonRepository.findById(lessonUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonUpdateDTO.getId()));

        // Validation doublon nom
        if (lessonUpdateDTO.getName() != null &&
                !lesson.getName().equals(lessonUpdateDTO.getName()) &&
                lessonRepository.existsByNameAndIdNot(lessonUpdateDTO.getName(), lessonUpdateDTO.getId())) {
            throw new DuplicateResourceException("Lesson", "name", lessonUpdateDTO.getName());
        }

        // Mise à jour champs simples
        if (lessonUpdateDTO.getName() != null) lesson.setName(lessonUpdateDTO.getName());
        if (lessonUpdateDTO.getDescription() != null) lesson.setDescription(lessonUpdateDTO.getDescription());
        if (lessonUpdateDTO.getThumbnail() != null) lesson.setThumbnail(lessonUpdateDTO.getThumbnail());

        // Mise à jour cours
        if (lessonUpdateDTO.getCourId() != null) {
            Cour cour = courRepository.findById(lessonUpdateDTO.getCourId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cour", "id", lessonUpdateDTO.getCourId()));
            lesson.setCour(cour);
        }

        // Mise à jour des vidéos (APPROCHE ADDITIVE)
        if (lessonUpdateDTO.getVideos() != null) {
            updateVideosAdditive(lesson, lessonUpdateDTO.getVideos());
        }

        // Suppression explicite des vidéos
        if (lessonUpdateDTO.getVideosToDelete() != null && !lessonUpdateDTO.getVideosToDelete().isEmpty()) {
            deleteVideosExplicit(lesson, lessonUpdateDTO.getVideosToDelete());
        }

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Leçon mise à jour : {}", savedLesson.getName());
        return lessonMapper.toDTO(savedLesson);
    }

    // APPROCHE ADDITIVE : Seules les vidéos envoyées sont modifiées/ajoutées
    private void updateVideosAdditive(Lesson lesson, List<LessonVideoItemUpdateDTO> videosToUpdate) {
        for (LessonVideoItemUpdateDTO videoDTO : videosToUpdate) {
            if (videoDTO.getId() != null) {
                // MISE À JOUR vidéo existante
                updateExistingVideo(lesson, videoDTO);
            }
        }
    }

    private void updateExistingVideo(Lesson lesson, LessonVideoItemUpdateDTO videoDTO) {
        // Trouver la vidéo dans la lesson
        LessonVideoItem existingVideo = lesson.getVideos().stream()
                .filter(v -> v.getId().equals(videoDTO.getId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Video", "id", videoDTO.getId()));

        // Mise à jour avec MapStruct
        lessonVideoItemMapper.updateFromDTO(videoDTO, existingVideo);
    }

    private void deleteVideosExplicit(Lesson lesson, List<Long> videoIdsToDelete) {
        // Filtrer les vidéos à supprimer
        List<LessonVideoItem> videosToRemove = lesson.getVideos().stream()
                .filter(video -> videoIdsToDelete.contains(video.getId()))
                .collect(Collectors.toList());

        // Supprimer de la collection
        lesson.getVideos().removeAll(videosToRemove);
        // Supprimer de la base de données
        lessonVideoItemRepository.deleteAll(videosToRemove);
    }

    @Override
    public List<LessonDTO> getAllLesson() {
        log.debug("Tentative de récupération de tous les Lesçons");
        List<Lesson> lessons = lessonRepository.findAll();
        log.info("{} lessons récupérés avec succès", lessons.size());
        return lessons.stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO getLessonById(Long id) {
        log.debug("Tentative de récupération de Lesçon ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        log.info("lesson récupéré avec id {} est {}", lesson.getId(),lesson.getName());
        return lessonMapper.toDTO(lesson); // ← PROPRE et CONCIS
    }

   @Override
    public List<LessonDTO> getLessonsByCourId(Long courId) {
       log.debug("Tentative de récupération des Lesçons  de cour ID: {}", courId);
        List<Lesson> lessons = lessonRepository.findByCourId(courId);
       log.info("{} Tous les lessons récupérés de cours sont ", lessons.size());
        return lessons.stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO getLessonByName(String name) {
        log.debug("Tentative de récupération de Lesçon avec le nom: {}", name);
        Lesson lesson = lessonRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "name", name));

        log.info("{} lessons récupérés avec ce noms", lesson.getName());
        return lessonMapper.toDTO(lesson);
    }

    @Override
    public LessonDelete deleteLesson(Long id) {
        log.debug("Tentative de suppression du leçon ID: {}", id);
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Tentative de suppression d'une leçon inexistante ID: {}", id);
                    return new ResourceNotFoundException("Lesson", "id", id);
                });
        //String lessonName = lesson.getName();
        try {
            lessonRepository.deleteById(id);
            log.info("leçon {} - '{}' supprimée avec succès", id, lesson.getName());
            return new LessonDelete(
                    "Suppression réussie",
                    lesson.getName(),
                    lesson.getId()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du leçon ID: {} - {}", id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du leçon", e);
        }
    }
}
