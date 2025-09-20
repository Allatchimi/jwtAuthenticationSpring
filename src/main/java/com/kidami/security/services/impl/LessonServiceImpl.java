package com.kidami.security.services.impl;

import com.kidami.security.dto.lessonDTO.*;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.LessonMapper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl implements LessonService {

    private static final Logger log = LoggerFactory.getLogger(LessonServiceImpl.class);
    private final LessonRepository lessonRepository;
    private final CourRepository courRepository;
    private final LessonMapper lessonMapper;
    private final LessonVideoItemMapper lessonVideoItemMapper;
    private final LessonVideoItemRepository lessonVideoItemRepository;
    private final StorageService storageService;

    public LessonServiceImpl(LessonRepository lessonRepository,
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
    @Transactional
    public LessonDTO addLesson(LessonSaveDTO lessonSaveDTO, MultipartFile imageFile, List<MultipartFile> videoFiles) {
        log.debug("Création d'une leçon: {}", lessonSaveDTO.getName());

        if (lessonRepository.existsByName(lessonSaveDTO.getName())) {
            throw new DuplicateResourceException("Lesson", "name", lessonSaveDTO.getName());
        }

        Cour cour = courRepository.findById(lessonSaveDTO.getCourId())
                .orElseThrow(() -> new ResourceNotFoundException("Cour", "id", lessonSaveDTO.getCourId()));

        Lesson lesson = lessonMapper.fromSaveDTO(lessonSaveDTO);
        lesson.setCour(cour);

        // Image
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageName = storageService.saveImage(imageFile, "lessons");
                lesson.setThumbnail("api/" + imageName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // Vidéos
        if (videoFiles != null && !videoFiles.isEmpty()) {
            List<LessonVideoItem> videoItems = videoFiles.stream()
                    .map(file -> {
                        LessonVideoItem item = new LessonVideoItem();
                        try {
                            String videoName = storageService.saveVideo(file, "lessons");
                            item.setUrl("api/" + videoName);
                            item.setLesson(lesson);
                        } catch (Exception e) {
                            throw new RuntimeException("Erreur sauvegarde vidéo: " + file.getOriginalFilename(), e);
                        }
                        return item;
                    }).collect(Collectors.toList());
            lesson.setVideos(videoItems);
        }

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Leçon créée: {} (ID: {})", savedLesson.getName(), savedLesson.getId());
        return lessonMapper.toDTO(savedLesson);
    }

    @Override
    @Transactional
    public LessonDTO updateLesson(LessonUpdateDTO lessonUpdateDTO) {
        Lesson lesson = lessonRepository.findById(lessonUpdateDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonUpdateDTO.getId()));

        if (lessonUpdateDTO.getName() != null &&
                !lesson.getName().equals(lessonUpdateDTO.getName()) &&
                lessonRepository.existsByNameAndIdNot(lessonUpdateDTO.getName(), lessonUpdateDTO.getId())) {
            throw new DuplicateResourceException("Lesson", "name", lessonUpdateDTO.getName());
        }

        if (lessonUpdateDTO.getName() != null) lesson.setName(lessonUpdateDTO.getName());
        if (lessonUpdateDTO.getDescription() != null) lesson.setDescription(lessonUpdateDTO.getDescription());
        if (lessonUpdateDTO.getThumbnail() != null) lesson.setThumbnail(lessonUpdateDTO.getThumbnail());

        if (lessonUpdateDTO.getCourId() != null) {
            Cour cour = courRepository.findById(lessonUpdateDTO.getCourId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cour", "id", lessonUpdateDTO.getCourId()));
            lesson.setCour(cour);
        }

        // Gestion des vidéos
        if (lessonUpdateDTO.getVideos() != null) {
            lessonUpdateDTO.getVideos().forEach(videoDTO -> {
                if (videoDTO.getId() != null) {
                    LessonVideoItem existing = lesson.getVideos().stream()
                            .filter(v -> v.getId().equals(videoDTO.getId()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Video", "id", videoDTO.getId()));
                    lessonVideoItemMapper.updateFromDTO(videoDTO, existing);
                }
            });
        }

        if (lessonUpdateDTO.getVideosToDelete() != null) {
            List<LessonVideoItem> toRemove = lesson.getVideos().stream()
                    .filter(v -> lessonUpdateDTO.getVideosToDelete().contains(v.getId()))
                    .collect(Collectors.toList());
            lesson.getVideos().removeAll(toRemove);
            lessonVideoItemRepository.deleteAll(toRemove);
        }

        Lesson saved = lessonRepository.save(lesson);
        return lessonMapper.toDTO(saved);
    }

    @Override
    public List<LessonDTO> getAllLesson() {
        return lessonRepository.findAll().stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO getLessonById(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        return lessonMapper.toDTO(lesson);
    }

    @Override
    public List<LessonDTO> getLessonsByCourId(Long courId) {
        return lessonRepository.findByCourId(courId).stream()
                .map(lessonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonDTO getLessonByName(String name) {
        Lesson lesson = lessonRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "name", name));
        return lessonMapper.toDTO(lesson);
    }

    @Override
    public LessonDelete deleteLesson(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", id));
        lessonRepository.deleteById(id);
        return new LessonDelete("Suppression réussie", lesson.getName(), lesson.getId());
    }
}
