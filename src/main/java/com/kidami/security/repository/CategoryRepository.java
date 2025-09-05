package com.kidami.security.repository;

import com.kidami.security.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    Optional<Category> findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Category c WHERE c.categoryName = :name AND c.categoryId != :id")
    boolean existsByCategoryNameAndIdNot(@Param("name") String name, @Param("id") Integer id);
}