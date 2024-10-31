package com.kidami.security.dto;


import com.kidami.security.models.LessonVideoItem;
import com.kidami.security.responses.LessonVideoItemRep;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonSaveDTO {

    private  String name;
    private Integer courId;
    private String thumbnail;
    private String description;
    private List<LessonVideoItemRep> video;
}
