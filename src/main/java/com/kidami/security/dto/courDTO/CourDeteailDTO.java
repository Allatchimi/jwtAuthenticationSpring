package com.kidami.security.dto.courDTO;

import com.kidami.security.models.Category;
import lombok.Data;


@Data
public class CourDeteailDTO {

    private Integer id;
    private Integer score;
    private Integer lessonNum;
    private Integer videoLen;
    private Integer downNum;
    private Integer follow;
    private Category categorie;
    private String userToken;
    private String name;
    private String description;
    private String thumbnail;
    private Double price;
    private String amountTotal;

}
