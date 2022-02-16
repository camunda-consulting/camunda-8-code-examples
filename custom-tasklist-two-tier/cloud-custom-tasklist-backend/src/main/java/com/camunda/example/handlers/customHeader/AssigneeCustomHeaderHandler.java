package com.camunda.example.handlers.customHeader;

import com.camunda.example.model.UserTask;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AssigneeCustomHeaderHandler implements CustomHeaderHandler {
  @Override
  public Set<String> getHeaderNames() {
    return Set.of("io.camunda.zeebe:assignee");
  }

  @Override
  public void handle(UserTask userTask, String value) {
    userTask.setAssignee(value);
  }
}
