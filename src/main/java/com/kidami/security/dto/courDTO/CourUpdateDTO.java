package com.kidami.security.dto.courDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private BigDecimal price;
    private String amountTotal;

}