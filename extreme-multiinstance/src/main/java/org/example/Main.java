package org.example;

import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.ZeebeDeployment;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableZeebeClient
@ZeebeDeployment(resources = "classpath*:**.bpmn")
public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @ZeebeWorker(
      type = "generateUuids",
      autoComplete = true,
      fetchVariables = {"uuid"})
  public Map<String, Object> generateUuids() {
    return createUuids("uuids");
  }

  @ZeebeWorker(type = "generateInnerUuids", autoComplete = true, fetchVariables = {"uuid"})
  public Map<String, Object> generateInnerUuids() {
    return createUuids("inneruuids");
  }

  @ZeebeWorker(type = "logger",autoComplete = true,fetchVariables = {"inneruuid"})
  public Map<String, Object> logger(){
    return Map.of("timestamp", LocalDateTime.now().toString());
  }

  private Map<String, Object> createUuids(String variableName) {
    LOG.info("Generating UUIDs");
    return Map.of(
        variableName,
        IntStream.range(0, 1000)
            .mapToObj(i -> UUID.randomUUID().toString())
            .collect(Collectors.toList()));
  }
}
