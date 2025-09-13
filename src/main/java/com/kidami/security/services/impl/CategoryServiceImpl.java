package com.kidami.security.services.impl;

import com.kidami.security.dto.categoryDTO.CategoryDTO;
import com.kidami.security.dto.categoryDTO.CategorySaveDTO;
import com.kidami.security.dto.categoryDTO.CategoryUpdateDTO;
import com.kidami.security.exceptions.DuplicateResourceException;
import com.kidami.security.exceptions.ResourceNotFoundException;
import com.kidami.security.mappers.CategoryMapper;
import com.kidami.security.models.Category;


import com.kidami.security.repository.CategoryRepository;
import com.kidami.security.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryMapper categoryMapper, CategoryRepository categoryRepository) {
        this.categoryMapper = categoryMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO addCategory(CategorySaveDTO categorySaveDTO) {
        log.debug("Tantative de creation de Categorie {}", categorySaveDTO);

        if(categorySaveDTO.getCategoryName() == null || categorySaveDTO.getCategoryName().trim().isEmpty() ){
            throw new IllegalArgumentException("Le nom du Categorie est obligatoire");
        }
        if (categoryRepository.existsByCategoryName(categorySaveDTO.getCategoryName())) {
            log.warn("Tentative de création d'un categorie en double: {}", categorySaveDTO.getCategoryName());
            throw new DuplicateResourceException("Course", "name", categorySaveDTO.getCategoryName());
        }

        try {
            Category  category = categoryMapper.fromSaveDTO(categorySaveDTO);
            Category categorySaved =  categoryRepository.save(category);
            log.info("créé avec succès : {}", categorySaved.getCategoryName());
            return  categoryMapper.toDTO(categorySaved);

        } catch (DuplicateResourceException e) {
            // On laisse remonter les exceptions métier
            throw e;
        }  catch (Exception e) {
            log.error("Erreur inattendue lors de la création du categorie: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création du categorie", e);
        }

    }

    @Override
    public List<CategoryDTO> getAllCategory() {
        log.debug("Tantative de recuperation de tout les categories");
        List<Category> getAllCategorys = categoryRepository.findAll();

        log.info("{} categories récupérés avec succès", getAllCategorys.size());
        return getAllCategorys.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());

    }

    @Override
    public CategoryDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO) {

        log.debug("mise a jour de ctegorie {}", categoryUpdateDTO.getCategoryName());
        Category category = categoryRepository.findById(categoryUpdateDTO.getCategoryId())
                .orElseThrow(()->{
                    log.warn("le categorie n existe pas : {}", categoryUpdateDTO.getCategoryName());
                    return new ResourceNotFoundException("Category", "id", categoryUpdateDTO.getCategoryId());
                });

        // Vérifier si le nouveau nom existe déjà (pour un autre cours)
        if (categoryUpdateDTO.getCategoryName() != null &&
                !category.getCategoryName().equals(categoryUpdateDTO.getCategoryName()) &&
                categoryRepository.existsByCategoryNameAndIdNot(categoryUpdateDTO.getCategoryName(), categoryUpdateDTO.getCategoryId())) {
            log.warn("le nouveau nom existe déjà pour un autre categorie: {}", category.getCategoryName());
            throw new DuplicateResourceException("Category", "name", categoryUpdateDTO.getCategoryName());
        }
        log.trace("Données de mise à jour valides: {}", categoryUpdateDTO);
        try {
            if(categoryUpdateDTO.getCategoryName() != null) category.setCategoryName(categoryUpdateDTO.getCategoryName());

            Category updatedCategry = categoryRepository.save(category);
            log.info("le categorie a ete bien mise a jour : {}", updatedCategry);
            return categoryMapper.toDTO(updatedCategry);
        }catch (Exception e) {
            log.error("Erreur lors de la mise a jour du categorie: {}", e.getMessage());
            throw e;
        }


    }

    @Override
    public boolean deleteCategory(Long id) {
        log.debug("Tentative de suppression du Categorie ID: {}", id);

        if (!categoryRepository.existsById(id)) {
            log.warn("Tentative de suppression d'un Categorie inexistant ID: {}", id);
            throw new ResourceNotFoundException("Category", "id", id);
        }
        try {

            categoryRepository.deleteById(id);
            log.info("Categorie supprimé avec succès ID: {}", id);
            return true;
        }
        catch (Exception e) {
            log.error("Erreur lors de la suppression du categorie ID: {} - {}", id, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression du categorie", e);

        }


    }

}
