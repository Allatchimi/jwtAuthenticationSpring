package com.kidami.security.dto;

import com.kidami.security.models.Cour;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class CategorySaveDTO {

    private String categoryName;
    //@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private Collection<Cour> cour;
}
