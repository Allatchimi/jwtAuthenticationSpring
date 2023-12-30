package com.kidami.security.dto;

import com.kidami.security.models.Cour;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryUpdateDTO {
    private Long categoryId;
    private String categoryName;
    @OneToMany(mappedBy = "category")
    private List<Cour> cour;
}
