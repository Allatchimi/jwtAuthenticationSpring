package com.kidami.security.repository;

import com.kidami.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //Optional<User> findByEmail(String email);

   User findFirstByEmail(String email);
}
