package com.camunda.example;

import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ZeebeController {

  @ZeebeWorker(type = "service-task", autoComplete = true)
  public void serviceTask() {

  }

}
