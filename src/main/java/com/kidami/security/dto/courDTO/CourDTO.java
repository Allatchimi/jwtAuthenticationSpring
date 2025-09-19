package com.kidami.security.dto.courDTO;

import com.kidami.security.models.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourDTO {
    private Integer id;
    private Integer score;
    private Integer lessonNum;
    private Integer videoLen;
    private Integer downNum;
    private Integer follow;
    private String categoryName;
    private String userToken;
    private String name;
    private String description;
    private String thumbnail;
    private BigDecimal price;
    private String amountTotal;
    private String teacherName;

}
