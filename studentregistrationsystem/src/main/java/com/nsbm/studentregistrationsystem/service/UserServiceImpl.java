package com.nsbm.studentregistrationsystem.service;

import com.nsbm.studentregistrationsystem.model.User;
import com.nsbm.studentregistrationsystem.repository.UserRepository;
import com.nsbm.studentregistrationsystem.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User saveUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 🔐 ENCRYPT PASSWORD
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // default values
        user.setVerified(false);

        return userRepository.save(user);
    }
}
