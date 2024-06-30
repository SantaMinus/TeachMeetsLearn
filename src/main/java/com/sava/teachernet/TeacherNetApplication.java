package com.sava.teachernet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:/Users/sava/.env")
public class TeacherNetApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeacherNetApplication.class, args);
	}

}
