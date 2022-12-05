package com.localweb.wagecalculatorapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Configuration
@SpringBootApplication
@EnableScheduling
@Component
public class WageCalculatorAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WageCalculatorAppApplication.class, args);
	}

}
