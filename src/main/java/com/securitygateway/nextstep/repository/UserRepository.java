package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // From NextStep
    Optional<User> findByEmail(String email);

    // From your SRS project
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
