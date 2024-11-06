package com.kidami.security.controllers;

import com.kidami.security.dto.CategoryDTO;
import com.kidami.security.dto.CategorySaveDTO;
import com.kidami.security.dto.CategoryUpdateDTO;

import com.kidami.security.models.Category;
import com.kidami.security.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/saveCategory")
    @ResponseBody
    public Category saveCour(@RequestBody CategorySaveDTO categorySaveDTO){

        Category id = categoryService.addCategory(categorySaveDTO);
        return id;

    }
    @GetMapping("/getAllCategorys")
    @ResponseBody
    List<CategoryDTO> getAllCategory(){
        List<CategoryDTO> allCategorys = categoryService.getAllCategory();

        return allCategorys;
    }
    @PutMapping("/updateCategory")
    @ResponseBody
    public  String updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO){
        String id = categoryService.updateCategory(categoryUpdateDTO);
        return id;
    }
    @DeleteMapping("/deleteCategoryId/{id}")
    public  String deleteCategory(@PathVariable(value="id") Integer id){
        boolean deletecategory = categoryService.deleteCategory(id);
        return "deleted!!!!!!!!";
    }

}