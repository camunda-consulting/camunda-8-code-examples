package com.camunda.example;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = {"classpath*:**.bpmn","classpath*:**.dmn"})
public class CloudTasklistApplication {
  public static void main(String[] args) {
    SpringApplication.run(CloudTasklistApplication.class, args);
  }
}
