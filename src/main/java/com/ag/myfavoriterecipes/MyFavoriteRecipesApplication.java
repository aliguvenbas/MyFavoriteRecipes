package com.ag.myfavoriterecipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MyFavoriteRecipesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyFavoriteRecipesApplication.class, args);
	}

}
