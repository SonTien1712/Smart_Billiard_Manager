package com.BillardManagement;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BillardManagementApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BillardManagementApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
