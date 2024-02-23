package com.psj.itembrowser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ItemBrowserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemBrowserApplication.class, args);
	}
}