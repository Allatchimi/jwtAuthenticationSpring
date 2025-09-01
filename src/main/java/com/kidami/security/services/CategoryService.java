package com.kidami.security.services;

import com.kidami.security.dto.CategoryDTO;
import com.kidami.security.dto.CategorySaveDTO;
import com.kidami.security.dto.CategoryUpdateDTO;
import com.kidami.security.models.Category;

import java.util.List;

public interface CategoryService {

    CategoryDTO addCategory(CategorySaveDTO categorySaveDTO);
    List<CategoryDTO> getAllCategory();
    CategoryDTO updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    boolean deleteCategory(Integer id);
}