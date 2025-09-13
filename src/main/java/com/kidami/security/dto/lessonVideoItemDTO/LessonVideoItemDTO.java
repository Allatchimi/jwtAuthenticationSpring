package com.kidami.security.dto.lessonVideoItemDTO;

import lombok.Data;

@Data
public class LessonVideoItemDTO {
    private Long id;
    private String name;
    private String url;
    private String thumbnail;
    private Integer duration;
    private Integer orderIndex;
    private Long lessonId;
}