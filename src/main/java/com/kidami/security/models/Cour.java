package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name= "cours")
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courId")
    private Integer id;
    @Column(name = "score")
    private Integer score;
    @Column(name = "userToken")
    private String userToken;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "thumbnail")
    private String thumbnail;
    @Column(name = "video")
    private String video;
    @Column(name = "price")
    private String price;
    @Column(name = "amountTotal")
    private String amountTotal;
    @Column(name = "lessonNum")
    private Integer lessonNum;
    @Column(name = "videoLen")
    private Integer videoLen;
    @Column(name = "downNum")
    private Integer downNum;
    @Column(name = "follow")
    private Integer follow;
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Category categorie;
   // @Column(name = "type_id")
   // private Integer type_id;


}
