package com.example.StudentRegistrationSystem;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class StudentRegistrationSystemApplicationTests {

	@Test
	void contextLoads() {
	}

}
