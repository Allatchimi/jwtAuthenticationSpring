package com.kidami.security.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name= "cours")
public class Cour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cour_Id")
    private Integer id;
    @Column(name = "score")
    private Integer score;
    @Column(name = "user_Token")
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
    @Column(name = "amount_Total")
    private String amountTotal;
    @Column(name = "lesson_Num")
    private Integer lessonNum;
    @Column(name = "video_Len")
    private Integer videoLen;
    @Column(name = "down_Num")
    private Integer downNum;
    @Column(name = "follow")
    private Integer follow;
    @ManyToOne
    @JoinColumn(name = "categorie_id")
    private Category categorie;
  

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Cour cour = (Cour) o;
        return getId() != null && Objects.equals(getId(), cour.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
