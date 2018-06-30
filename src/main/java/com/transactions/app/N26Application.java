package com.transactions.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = { "com.transactions" })
public class N26Application {

	public static void main(String[] args) {
		SpringApplication.run(N26Application.class, args);
	}
}
