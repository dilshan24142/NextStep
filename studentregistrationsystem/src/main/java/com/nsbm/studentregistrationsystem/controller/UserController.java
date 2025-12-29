package com.nsbm.studentregistrationsystem.controller;

import com.nsbm.studentregistrationsystem.model.User;
import com.nsbm.studentregistrationsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // for frontend / Postman
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ✅ SAVE USER
    @PostMapping("/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {

        User savedUser = userService.saveUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
