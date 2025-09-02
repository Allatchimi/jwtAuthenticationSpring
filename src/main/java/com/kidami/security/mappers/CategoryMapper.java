package com.kidami.security.mappers;


import com.kidami.security.dto.categoryDTO.CategoryDTO;
import com.kidami.security.dto.categoryDTO.CategorySaveDTO;
import com.kidami.security.models.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDTO(Category category);
    Category toCategory(CategoryDTO categoryDTO);
    Category fromSaveDTO(CategorySaveDTO categorySaveDTO);

}
