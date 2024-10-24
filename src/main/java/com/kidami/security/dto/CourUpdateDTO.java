package com.kidami.security.dto;

import com.kidami.security.models.Category;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourUpdateDTO {
    private Integer id;
    private Integer score;
    private String userToken;
    private String name;
    private String description;
    private String thumbnail;
    private String video;
    private String price;
    private String amountTotal;
    private Integer lessonNum;
    private Integer videoLen;
    private Integer downNum;
    private Integer follow;
    private Integer type_id;
}
