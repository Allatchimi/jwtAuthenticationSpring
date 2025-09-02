package com.kidami.security.dto.lessonVideoItemDTO;

import lombok.Data;

@Data
public class LessonVideoItemSaveDTO {
    private String name;     // ← Correspond au JSON "name"
    private String url;      // ← Correspond au JSON "url"
    private String thumbnail;
    private Integer duration;
    private Integer orderIndex;
}