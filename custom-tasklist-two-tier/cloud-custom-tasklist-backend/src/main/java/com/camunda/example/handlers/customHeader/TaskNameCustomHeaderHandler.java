package com.camunda.example.handlers.customHeader;

import com.camunda.example.model.UserTask;

import java.util.Set;

public class TaskNameCustomHeaderHandler implements CustomHeaderHandler{
  @Override
  public Set<String> getHeaderNames() {
    return Set.of("taskName");
  }

  @Override
  public void handle(UserTask userTask, String value) throws Exception {
    userTask.setTaskName(value);
  }
}
