package com.kidami.security.dto.courDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourSaveDTO {
    private Integer score;
    private Integer lessonNum;
    private Integer videoLen;
    private Integer downNum;
    private Integer follow;
    @NotNull(message = "L'ID de catégorie est obligatoire")
    private Integer categorieId; // ID de la catégorie, pas l'objet complet
    private String userToken;
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String name;
    private String description;
    private Double price;
    private String amountTotal;
}