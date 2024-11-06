package com.kidami.security.responses;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class CourseResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public CourseResponseEntity() {}

    public CourseResponseEntity(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
