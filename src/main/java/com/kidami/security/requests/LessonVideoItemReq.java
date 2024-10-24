package com.kidami.security.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonVideoItemReq {
    private Integer id;
    private String name;
    private String url;
    private String thumbnail;
}
