package com.example.searchservice;

import org.springframework.boot.SpringApplication;

public class TestSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(SearchServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
