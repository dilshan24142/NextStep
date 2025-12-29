package com.nsbm.studentregistrationsystem.repository;

import com.nsbm.studentregistrationsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
}
