package com.kidami.security.services;

import com.kidami.security.dto.categoryDTO.CategoryDTO;
import com.kidami.security.dto.categoryDTO.CategorySaveDTO;
import com.kidami.security.dto.categoryDTO.CategoryUpdateDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO addCategory(CategorySaveDTO categorySaveDTO);
    List<CategoryDTO> getAllCategory();
    CategoryDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    boolean deleteCategory(Integer id);
}