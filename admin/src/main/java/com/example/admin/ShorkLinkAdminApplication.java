package com.example.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@MapperScan("com.example.admin.mapper")
@EnableDiscoveryClient
@EnableFeignClients("com.example.admin.remote")
@SpringBootApplication
public class ShorkLinkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShorkLinkAdminApplication.class, args);
    }
}
