package com.kidami.security.models;

import jakarta.persistence.*;

@Entity
@Table(name= "cour")
public class Cour {
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
    @Column(name = "type_id")
    private Integer type_id;

    public Integer getType_id() {
        return type_id;
    }

    public void setType_id(Integer type_id) {
        this.type_id = type_id;
    }

    @Column(name = "score")
    private Integer score;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courId")
    private Integer id;


    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id" ,referencedColumnName = "categoryId")
    private Category category;

    public Cour() {
    }

    public Cour(String userToken, String name, String description, String thumbnail,
                      String video, String price, String amountTotal, Integer lessonNum,
                      Integer videoLen, Integer downNum, Integer follow, Integer score, Integer id) {
        this.userToken = userToken;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.video = video;
        this.price = price;
        this.amountTotal = amountTotal;
        this.lessonNum = lessonNum;
        this.videoLen = videoLen;
        this.downNum = downNum;
        this.follow = follow;
        this.score = score;
        this.id = id;
    }


}
