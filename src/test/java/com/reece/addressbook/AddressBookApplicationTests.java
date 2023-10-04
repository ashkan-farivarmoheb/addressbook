package com.reece.addressbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.reece.addressbook"})
public class AddressBookApplicationTests {

    public static void main(String[] args) {
        System.getProperty("spring.profiles.active", "test");
        System.getProperty("server.port", "8091");
        SpringApplication.from(AddressBookApplication::main)
                .run(args);
    }

}