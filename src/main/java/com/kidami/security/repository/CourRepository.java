package com.kidami.security.repository;
import com.kidami.security.models.Cour;
import com.kidami.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourRepository extends JpaRepository<Cour, Long> , JpaSpecificationExecutor<Cour> {

    Optional<Cour> findById(Long id);
    boolean existsById(Long id);
    boolean existsByName(String name);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cour c WHERE c.name = :name AND c.id != :id")
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
    List<Cour> findByTeacher(User teacher);
    List<Cour> findTop10ByOrderByEnrollmentCountDesc();
    @Query("select c from Cour c where c.name like :kw")
    List<Cour> searchCour(@Param("kw") String keyword);

}