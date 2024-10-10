package com.kidami.security.repository;

import com.kidami.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    Optional<User> findByFirstnameOrEmail(String username, String email);

    Optional<User> findByFirstname(String username);

    Boolean existsByFirstname(String username);

    Boolean existsByEmail(String email);

}
