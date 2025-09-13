package com.kidami.security.controllers;

import com.kidami.security.dto.categoryDTO.CategoryDTO;
import com.kidami.security.dto.categoryDTO.CategorySaveDTO;
import com.kidami.security.dto.categoryDTO.CategoryUpdateDTO;

import com.kidami.security.responses.ApiResponse;
import com.kidami.security.services.CategoryService;
import com.kidami.security.utils.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/categorys")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/saveCategory")
    public ResponseEntity<ApiResponse<CategoryDTO>> saveCategory(@RequestBody CategorySaveDTO categorySaveDTO){
        CategoryDTO categoryDTO = categoryService.addCategory(categorySaveDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseUtil.created("Category create succes",categoryDTO,null));

    }

    @GetMapping("/getAllCategorys")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllCategory(){
        List<CategoryDTO> allCategorys = categoryService.getAllCategory();
        if(allCategorys.isEmpty()) {
            return ResponseEntity.ok(
                    ResponseUtil.success("No Categorys found", Collections.emptyList(), null));
        }
        return  ResponseEntity.ok(ResponseUtil.success("Categorys  retrieved successfully",allCategorys,null));
    }

    @PutMapping("/updateCategory")
    public  ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@RequestBody CategoryUpdateDTO categoryUpdateDTO){
        CategoryDTO categoryDTO = categoryService.updateCategory(categoryUpdateDTO);
        return ResponseEntity.ok().body(ResponseUtil.success("Category updated successfully",categoryDTO,null));
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable(value="id") Long id){
        boolean isDeleted = categoryService.deleteCategory(id);
        if (isDeleted) {
            return ResponseEntity.ok(ResponseUtil.success("Category deleted successfully", null, null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseUtil.error("Category not found", null, null));
        }
    }
}