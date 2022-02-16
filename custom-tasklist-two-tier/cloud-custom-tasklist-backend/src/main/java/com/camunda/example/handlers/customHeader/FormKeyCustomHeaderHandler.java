package com.camunda.example.handlers.customHeader;

import com.camunda.example.model.UserTask;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class FormKeyCustomHeaderHandler implements CustomHeaderHandler {
  @Override
  public Set<String> getHeaderNames() {
    return Set.of("io.camunda.zeebe:formKey","formKey");
  }

  @Override
  public void handle(UserTask userTask, String value) throws Exception {
    userTask.setFormKey(value);
  }
}
