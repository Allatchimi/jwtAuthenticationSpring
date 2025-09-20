package com.kidami.security.services.impl;

import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemSaveDTO;
import com.kidami.security.dto.lessonVideoItemDTO.LessonVideoItemUpdateDTO;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.LessonVideoItemMapper;
import com.kidami.security.models.Lesson;
import com.kidami.security.models.LessonVideoItem;
import com.kidami.security.repository.LessonRepository;
import com.kidami.security.repository.LessonVideoItemRepository;
import com.kidami.security.services.LessonVideoItemService;
import com.kidami.security.services.StorageService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonVideoItemServiceImpl implements LessonVideoItemService {

    private static final Logger log = LoggerFactory.getLogger(LessonVideoItemServiceImpl.class);
    private final LessonVideoItemRepository lessonVideoItemRepository;
    private final LessonRepository lessonRepository;
    private final LessonVideoItemMapper lessonVideoItemMapper;
    private final StorageService storageService;

    public LessonVideoItemServiceImpl(LessonVideoItemRepository lessonVideoItemRepository,
                                      LessonRepository lessonRepository,
                                      LessonVideoItemMapper lessonVideoItemMapper,
                                      StorageService storageService) {
        this.lessonVideoItemRepository = lessonVideoItemRepository;
        this.lessonRepository = lessonRepository;
        this.lessonVideoItemMapper = lessonVideoItemMapper;
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public LessonVideoItemDTO addLessonVideoItem(Long lessonId,
                                                 LessonVideoItemSaveDTO dto,
                                                 MultipartFile videoFile,
                                                 MultipartFile imageFile) {
        log.debug("Tantative de récupération de la leçon {}", lessonId);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", "id", lessonId.toString()));

        if (lessonVideoItemRepository.existsByName(dto.getName())) {
            log.warn("Il y a déjà un video item avec le nom {}", dto.getName());
            throw new DuplicateResourceException("LessonVideoItem", "name", dto.getName());
        }

        try {
            LessonVideoItem item = lessonVideoItemMapper.fromSaveDTO(dto);
            item.setLesson(lesson);
            item.setUrl(handleVideoUpload(videoFile));
            item.setThumbnail(handleImageUpload(imageFile));

            LessonVideoItem savedItem = lessonVideoItemRepository.save(item);
            log.info("Video item {} ajouté avec succès", savedItem.getName());
            return lessonVideoItemMapper.toDTO(savedItem);

        } catch (RuntimeException e) {
            log.error("Erreur lors de l'ajout du video item", e);
            throw e;
        }
    }

    private String handleVideoUpload(MultipartFile videoFile) {
        if (videoFile == null || videoFile.isEmpty())
            throw new IllegalArgumentException("Le fichier vidéo est obligatoire");
        try {
            return "api/" + storageService.saveVideo(videoFile, "lessons");
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload vidéo", e);
        }
    }

    private String handleImageUpload(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) return null;
        try {
            return "api/" + storageService.saveImage(imageFile, "videoItems");
        } catch (Exception e) {
            throw new RuntimeException("Erreur upload image", e);
        }
    }

    @Override
    public List<LessonVideoItemDTO> getAllLessonVideoItem() {
        List<LessonVideoItem> items = lessonVideoItemRepository.findAll();
        log.info("Nombre total de video items : {}", items.size());
        return items.stream()
                .map(lessonVideoItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LessonVideoItemDTO updateLessonVideoItem(LessonVideoItemUpdateDTO dto) {
        LessonVideoItem item = lessonVideoItemRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("LessonVideoItem", "id", dto.getId()));

        if (dto.getName() != null && !dto.getName().equals(item.getName())
                && lessonVideoItemRepository.existsByNameAndIdNot(dto.getName(), dto.getId())) {
            throw new DuplicateResourceException("LessonVideoItem", "name", dto.getName());
        }

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getUrl() != null) item.setUrl(dto.getUrl());
        if (dto.getThumbnail() != null) item.setThumbnail(dto.getThumbnail());
        if (dto.getDuration() != null) item.setDuration(dto.getDuration());
        if (dto.getOrderIndex() != null) item.setOrderIndex(dto.getOrderIndex());

        LessonVideoItem updatedItem = lessonVideoItemRepository.save(item);
        log.info("Video item ID {} mis à jour", updatedItem.getId());
        return lessonVideoItemMapper.toDTO(updatedItem);
    }

    @Override
    public boolean deleteLessonVideoItem(Long id) {
        LessonVideoItem item = lessonVideoItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LessonVideoItem", "id", id));

        try {
            if (item.getUrl() != null) storageService.deleteFile(item.getUrl().replace("api/", ""));
            if (item.getThumbnail() != null) storageService.deleteFile(item.getThumbnail().replace("api/", ""));
        } catch (Exception e) {
            log.warn("Impossible de supprimer les fichiers du video item ID {}", id, e);
        }

        lessonVideoItemRepository.deleteById(id);
        log.info("Video item ID {} supprimé avec succès", id);
        return true;
    }

    @Override
    public List<LessonVideoItemDTO> getVideoItemByLessonId(Long lessonId) {
        List<LessonVideoItem> items = lessonVideoItemRepository.findByLessonId(lessonId);
        log.info("Nombre de video items récupérés pour la leçon {} : {}", lessonId, items.size());
        return items.stream()
                .map(lessonVideoItemMapper::toDTO)
                .collect(Collectors.toList());
    }
}
