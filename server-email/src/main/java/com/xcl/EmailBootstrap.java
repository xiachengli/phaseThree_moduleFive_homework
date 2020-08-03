package com.xcl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EmailBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(EmailBootstrap.class,args);
    }
}
