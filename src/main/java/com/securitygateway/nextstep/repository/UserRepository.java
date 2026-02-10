package com.securitygateway.nextstep.repository;

import com.securitygateway.nextstep.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // NextStep login (email based authentication)
    Optional<User> findByEmail(String email);

    // Old Student Registration System support (username based)
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
