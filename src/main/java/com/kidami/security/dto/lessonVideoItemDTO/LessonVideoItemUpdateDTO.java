package com.kidami.security.dto.lessonVideoItemDTO;

import lombok.Data;

@Data
public class LessonVideoItemUpdateDTO {
    private Integer id;
    private String name;     // ← Correspond au JSON "name"
    private String url;      // ← Correspond au JSON "url"
    private String thumbnail;
    private Integer duration;
    private Integer orderIndex;
}
