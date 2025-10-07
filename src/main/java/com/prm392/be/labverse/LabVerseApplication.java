package com.prm392.be.labverse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LabVerseApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabVerseApplication.class, args);
	}

}
