package com.kidami.security.repository;

import com.kidami.security.models.AuthProvider;
import com.kidami.security.models.Category;
import com.kidami.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByName(String username);
    Boolean existsByName(String username);
    Boolean existsByEmail(String email);
    User findFirstByEmail(String email);
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM User c WHERE c.name = :name AND c.id != :id")
    boolean existsByUsernameAndIdNot(@Param("name") String name, @Param("id")  Long id);

    Optional<User> findByProviderAndProviderId(AuthProvider provider, String providerId);
    Boolean existsByProviderAndProviderId(AuthProvider provider, String providerId);
    // Méthode pour trouver par providerId seulement (utile pour Firebase)
    Optional<User> findByProviderId(String providerId);
}
