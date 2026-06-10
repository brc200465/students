package com.example.studentms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableAsync
@SpringBootApplication
@EnableRedisHttpSession
public class StudentmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(StudentmsApplication.class, args);
	}

}
