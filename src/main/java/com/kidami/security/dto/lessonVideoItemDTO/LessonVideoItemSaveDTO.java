package com.kidami.security.dto.lessonVideoItemDTO;

import lombok.Data;

@Data
public class LessonVideoItemSaveDTO {
    private String name;
    private String url;
    private String thumbnail;
    private Integer duration;
    private Integer orderIndex;
}