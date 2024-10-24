package com.kidami.security.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LessonVideoItemRep {

    private Integer id;
    private String name;
    private String url;
    private String thumbnail;

}
