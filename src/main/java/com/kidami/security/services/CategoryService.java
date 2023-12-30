package com.kidami.security.services;

import com.kidami.security.dto.CategoryDTO;
import com.kidami.security.dto.CategorySaveDTO;
import com.kidami.security.dto.CategoryUpdateDTO;

import java.util.List;

public interface CategoryService {

    String addCategory(CategorySaveDTO categorySaveDTO);
    List<CategoryDTO> getAllCategory();
    String updateCategory(CategoryUpdateDTO categoryUpdateDTO);

    boolean deleteCategory(Long id);
}
