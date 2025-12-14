package com.example.StudentRegistrationSystem;

import org.springframework.boot.SpringApplication;

public class TestStudentRegistrationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.from(StudentRegistrationSystemApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
