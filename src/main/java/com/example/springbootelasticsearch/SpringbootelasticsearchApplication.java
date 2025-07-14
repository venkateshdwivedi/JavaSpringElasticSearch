package com.example.springbootelasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableElasticsearchRepositories(basePackages = "com.example.springbootelasticsearch.repo")
public class SpringbootelasticsearchApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootelasticsearchApplication.class, args);
	}

}
