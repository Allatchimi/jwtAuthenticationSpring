package com.kidami.security.dto.courDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourUpdateDTO {
    private Long id;
    private Integer score;
    private Integer lessonNum;
    private Integer videoLen;
    private Integer downNum;
    private Integer follow;
    private Long categorieId; // Changé de Category à Integer pour l'ID
    private String userToken;
    private String name;
    private String description;
    private String thumbnail;
    private Double price;
    private String amountTotal;

}