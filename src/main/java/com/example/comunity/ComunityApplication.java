package com.example.comunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ComunityApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComunityApplication.class, args);
	}

}
