package com.kidami.security.services.impl;

import com.kidami.security.dto.CategoryDTO;
import com.kidami.security.dto.CategorySaveDTO;
import com.kidami.security.dto.CategoryUpdateDTO;
import com.kidami.security.models.Category;
import com.kidami.security.repository.CategoryRepository;
import com.kidami.security.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service

public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Override
    public String addCategory(CategorySaveDTO categorySaveDTO) {
        Category category = new Category(
                categorySaveDTO.getCategoryName(),
                categorySaveDTO.getCour()
        );
        categoryRepository.save(category);
        return category.getCategoryName();
    }

    @Override
    public List<CategoryDTO> getAllCategory() {

        List<Category> getAllCategorys = categoryRepository.findAll();
        List<CategoryDTO> categoryDTOList= new ArrayList<>();

        for(Category c:getAllCategorys){
            CategoryDTO categoryDTO = new CategoryDTO(
                    c.getCategoryId(),
                    c.getCategoryName(),
                    c.getCour()

            );
            categoryDTOList.add(categoryDTO);
        }
        return categoryDTOList;
    }

    @Override
    public String updateCategory(CategoryUpdateDTO categoryUpdateDTO) {

        if(categoryRepository.existsById(categoryUpdateDTO.getCategoryId())){
            Category category = categoryRepository.getReferenceById(categoryUpdateDTO.getCategoryId());
            category.setCategoryName(categoryUpdateDTO.getCategoryName());
            category.setCour(categoryUpdateDTO.getCour());

            categoryRepository.save(category);
        }else {
            System.out.println("Category not Exist");
        }
        return null;
    }

    @Override
    public boolean deleteCategory(Long id) {
        if(categoryRepository.existsById(id))
        {
            categoryRepository.deleteById(id);
        }
        else {
            System.out.println("category ID not exist");

        }
        return false;
    }

}
