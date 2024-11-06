package com.kidami.security.services;

import com.kidami.security.dto.CategoryDTO;
import com.kidami.security.dto.CategorySaveDTO;
import com.kidami.security.dto.CategoryUpdateDTO;
import com.kidami.security.models.Category;

import java.util.List;

public interface CategoryService {

    Category addCategory(CategorySaveDTO categorySaveDTO);
    List<CategoryDTO> getAllCategory();
    String updateCategory(CategoryUpdateDTO categoryUpdateDTO);
    boolean deleteCategory(Integer id);
}