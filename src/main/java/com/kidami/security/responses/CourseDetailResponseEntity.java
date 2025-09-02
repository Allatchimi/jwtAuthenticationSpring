package com.kidami.security.responses;

import com.kidami.security.dto.courDTO.CourDeteailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDetailResponseEntity {
    private int code;
    private String msg;
    private CourDeteailDTO data;
}
