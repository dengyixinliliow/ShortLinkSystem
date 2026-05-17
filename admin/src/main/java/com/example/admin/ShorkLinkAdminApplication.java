package com.example.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.admin.mapper")
@SpringBootApplication
public class ShorkLinkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShorkLinkAdminApplication.class, args);
    }
}
