package com.camunda.example.handlers.customHeader;

import com.camunda.example.model.UserTask;

import java.util.Set;

public interface CustomHeaderHandler {
  Set<String> getHeaderNames();

  void handle(UserTask userTask, String value) throws Exception;
}
