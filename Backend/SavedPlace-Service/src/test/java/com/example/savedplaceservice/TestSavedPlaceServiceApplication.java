package com.example.savedplaceservice;

import org.springframework.boot.SpringApplication;

public class TestSavedPlaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(SavedPlaceServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
