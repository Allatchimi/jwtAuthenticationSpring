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
import com.kidami.security.responses.LessonVideoItemRep;
import com.kidami.security.services.LessonVideoItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LessonVideoItemServiceImpl implements LessonVideoItemService {
    private static final Logger log = LoggerFactory.getLogger(LessonVideoItemServiceImpl.class);
    private final LessonVideoItemRepository lessonVideoItemRepository;
    private final LessonRepository lessonRepository;
    private final LessonVideoItemMapper lessonVideoItemMapper;

    public LessonVideoItemServiceImpl(LessonVideoItemRepository lessonVideoItemRepository, LessonRepository lessonRepository, LessonVideoItemMapper lessonVideoItemMapper) {
        this.lessonVideoItemRepository = lessonVideoItemRepository;
        this.lessonRepository = lessonRepository;
        this.lessonVideoItemMapper = lessonVideoItemMapper;
    }

    @Override
    public LessonVideoItemDTO addLessonVideoItem(Integer lessonId, LessonVideoItemSaveDTO lessonVideoItemSaveDTO) {
        log.debug("Tantative de recuperation de laçon {} ", lessonId);
        if (lessonVideoItemRepository.existsByName(lessonVideoItemSaveDTO.getName())) {
            log.warn("IL y a un video avec ce nom {}", lessonVideoItemSaveDTO.getName());
            throw new DuplicateResourceException("LessonVideoItem", "name", lessonVideoItemSaveDTO.getName());
        }

        LessonVideoItem lessonVideoItem = lessonVideoItemMapper.fromSaveDTO(lessonVideoItemSaveDTO);
        log.info("Video item {} : ajouter avec succés ", lessonVideoItem.getName());
        return lessonVideoItemMapper.toDTO(lessonVideoItem);
    }

    @Override
    public List<LessonVideoItemDTO> getAllLessonVideoItem() {
        log.debug("Tantative de recuperation de tout les video items");
        List<LessonVideoItem> lessonVideoItems = lessonVideoItemRepository.findAll();
        log.info("Video items : {}", lessonVideoItems.size());
        return lessonVideoItems.stream()
                .map(lessonVideoItemMapper::toDTO)
                .collect(Collectors.toList()
                );

    }

    @Override
    public LessonVideoItemDTO updateLessonVideoItem(LessonVideoItemUpdateDTO lessonVideoItemUpdateDTO) {
        log.debug("Tantative de mise à jours de {} ", lessonVideoItemUpdateDTO.getName());
        LessonVideoItem lessonVideoItem = lessonVideoItemRepository.findById(lessonVideoItemUpdateDTO.getId())
                .orElseThrow(() -> {
                    log.warn("le cours n existe pas: {}", lessonVideoItemUpdateDTO.getName());
                    return new ResourceNotFoundException("LessonVideoItem non trouvé avec ID : " + lessonVideoItemUpdateDTO.getId());
                });

        if (lessonVideoItemUpdateDTO.getName() != null
                && !lessonVideoItem.getName().equals(lessonVideoItemUpdateDTO.getName())
                && lessonVideoItemRepository.existsByNameAndIdNot(lessonVideoItemUpdateDTO.getName(), lessonVideoItemUpdateDTO.getId())) {
            log.warn("le nouveau nom existe deja pour un autre video item {}", lessonVideoItemUpdateDTO.getName());
            throw new DuplicateResourceException("LessonVideoItem", "name", lessonVideoItemUpdateDTO.getName());
        }

        log.trace("Données de mise à jour valides: {}", lessonVideoItemUpdateDTO);
        try {
            if (lessonVideoItemUpdateDTO.getName() != null) lessonVideoItem.setName(lessonVideoItemUpdateDTO.getName());
            if (lessonVideoItemUpdateDTO.getUrl() != null) lessonVideoItem.setUrl(lessonVideoItemUpdateDTO.getUrl());
            if (lessonVideoItemUpdateDTO.getThumbnail() != null)
                lessonVideoItem.setThumbnail(lessonVideoItemUpdateDTO.getThumbnail());
            if (lessonVideoItemUpdateDTO.getDuration() != null)
                lessonVideoItem.setDuration(lessonVideoItemUpdateDTO.getDuration());
            if (lessonVideoItemUpdateDTO.getOrderIndex() != null)
                lessonVideoItem.setOrderIndex(lessonVideoItemUpdateDTO.getOrderIndex());

            LessonVideoItem lessonVideoItemSave = lessonVideoItemRepository.save(lessonVideoItem);
            log.info("les video items : {} modifier avec succés", lessonVideoItemSave);
            return lessonVideoItemMapper.toDTO(lessonVideoItemSave);
        } catch (Exception e) {
            log.error("Erreur lors de la mise a jour du video item : {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean deleteLessonVideoItem(Integer id) {
        log.debug("Tentative de suppression du LessonVideoItem ID: {}", id);
        // Vérifiez si le cours existe
        if (!lessonVideoItemRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un video item  ID {} inexistant : ", id);
            throw new ResourceNotFoundException("LessonVideoItem", "id", id);
        }
        try {
            lessonVideoItemRepository.deleteById(id);
            log.info("Video item ID : {}  supprimé avec succès:", id);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du Video Item ID: {} - {}", id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du Video Item", e);
        }
    }

    @Override
    public List<LessonVideoItemDTO> getVideoItemByLessonId(Integer lessonId) {
        log.debug("Tentative de recuperation de la  liste des video items par leçon  ID: {}", lessonId);
        List<LessonVideoItem> lessonVideoItems= lessonVideoItemRepository.findByLessonId(lessonId);
        log.info("Video items  recuprer par lesçon sont au nombre de : {}", lessonVideoItems.size());
        return lessonVideoItems.stream()
                .map(lessonVideoItemMapper::toDTO)
                .collect(Collectors.toList());
    }

}


