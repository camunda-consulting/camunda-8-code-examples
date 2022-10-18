package org.example;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.EnableZeebeClient;
import io.camunda.zeebe.spring.client.annotation.Deployment;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
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
@Deployment(resources = "classpath*:**.bpmn")
public class Main {
  private static final Logger LOG = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) {
    SpringApplication.run(Main.class, args);
  }

  @JobWorker(
      type = "generateUuids",
      autoComplete = true,
      fetchVariables = {"uuid"})
  public Map<String, Object> generateUuids() {
    return createUuids("uuids");
  }

  @JobWorker(
      type = "generateInnerUuids",
      fetchVariables = {"uuid"})
  public Map<String, Object> generateInnerUuids() {
    return createUuids("inneruuids");
  }

  @JobWorker(
      type = "logger",
      fetchVariables = {"inneruuid"},
      autoComplete = false)
  public void logger(ActivatedJob job, JobClient client) {
    LOG.info("logger triggered");
    client
        .newCompleteCommand(job)
        .variables(Map.of("timestamp", LocalDateTime.now().toString()))
        .send();
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
