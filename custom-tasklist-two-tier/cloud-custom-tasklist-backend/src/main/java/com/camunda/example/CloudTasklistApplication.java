package com.camunda.example;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
public class CloudTasklistApplication {
  public static void main(String[] args) {
    SpringApplication.run(CloudTasklistApplication.class, args);
  }
}
