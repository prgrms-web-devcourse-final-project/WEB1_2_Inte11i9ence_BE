package com.prgrmsfinal.skypedia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SkypediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkypediaApplication.class, args);
    }

}
