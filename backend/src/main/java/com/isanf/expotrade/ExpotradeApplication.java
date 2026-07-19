package com.isanf.expotrade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExpotradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpotradeApplication.class, args);
    }
}
