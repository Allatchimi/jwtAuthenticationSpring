package com.kidami.security.dto;

import com.kidami.security.models.Category;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CourDTO {
    private Long id;
    private String title;
    private String description;
    @ManyToOne
    private Category category;
    private String content;
}
