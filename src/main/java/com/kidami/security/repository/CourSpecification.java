package com.kidami.security.repository;

import com.kidami.security.models.Cour;
import org.springframework.data.jpa.domain.Specification;

public class CourSpecification {

    public static Specification<Cour> hasKeyword(String keyword) {
        return (root, query, cb) ->
                keyword == null ? cb.conjunction() :
                        cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%");
    }

    public static Specification<Cour> hasPriceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("price"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), min);
            } else if (max != null) {
                return cb.lessThanOrEqualTo(root.get("price"), max);
            }
            return cb.conjunction();
        };
    }

    public static Specification<Cour> hasScore(Integer score) {
        return (root, query, cb) ->
                score == null ? cb.conjunction() :
                        cb.greaterThanOrEqualTo(root.get("score"), score);
    }

    public static Specification<Cour> hasCategory(String categoryName) {
        return (root, query, cb) ->
                categoryName == null ? cb.conjunction() :
                        cb.equal(root.get("categorie").get("id"), categoryName);
    }

    public static Specification<Cour> hasTeacher(String teacherName) {
        return (root, query, cb) ->
                teacherName == null ? cb.conjunction() :
                        cb.equal(root.get("teacher").get("id"), teacherName);
    }
}
