package com.kidami.security.controllers;

import com.kidami.security.dto.CategoryDTO;
import com.kidami.security.dto.CategorySaveDTO;
import com.kidami.security.dto.CategoryUpdateDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
@RestController
@RequestMapping( "api/v1/category")
public class CategoryController {
   // @Autowired
   // private CategoryService categoryService;
    @PostMapping("/saveCategory")
    public String saveCour(@RequestBody CategorySaveDTO categorySaveDTO){

      //  String id = categoryService.addCategory(categorySaveDTO);
        return id;

    }
    @GetMapping("/getAllCategorys")
    List<CategoryDTO> getAllCategory(){
        List<CategoryDTO> allCategorys = categoryService.getAllCategory();

        return allCategorys;
    }
    @PutMapping("/updateCategory")
    public  String updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO){
        String id = categoryService.updateCategory(categoryUpdateDTO);
        return id;
    }
    @DeleteMapping("/deleteCategoryId/{id}")
    public  String deleteCategory(@PathVariable(value="id") Long id){
        boolean deletecategory = categoryService.deleteCategory(id);
        return "deleted!!!!!!!!";
    }

}
*/