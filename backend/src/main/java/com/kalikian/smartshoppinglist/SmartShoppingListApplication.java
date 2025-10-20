package com.kalikian.smartshoppinglist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SmartShoppingListApplication {

    @GetMapping("/")
    public String home() {
        return "Hello World!";
    }

        public static void main(String[] args) {
            // Starts the Spring Boot application context
            SpringApplication.run(SmartShoppingListApplication.class, args);
    }
}