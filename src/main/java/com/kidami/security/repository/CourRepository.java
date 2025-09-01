package com.kidami.security.repository;
import com.kidami.security.models.Cour;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourRepository extends JpaRepository<Cour, Integer> {

    Optional<Cour> findById(Integer id);
    boolean existsById(Integer id);
    boolean existsByName(String name);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cour c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Integer id);


}