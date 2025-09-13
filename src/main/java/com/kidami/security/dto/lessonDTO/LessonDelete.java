package com.kidami.security.dto.lessonDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonDelete {
    private String message;
    private String deletedLessonName;
    private Long deletedLessonId;
}
