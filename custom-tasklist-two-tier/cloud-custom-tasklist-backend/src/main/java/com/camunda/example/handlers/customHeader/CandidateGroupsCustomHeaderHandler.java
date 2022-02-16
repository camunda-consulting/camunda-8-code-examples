package com.camunda.example.handlers.customHeader;

import com.camunda.example.model.UserTask;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class CandidateGroupsCustomHeaderHandler implements CustomHeaderHandler {
  private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<List<String>>() {};
  private final ObjectMapper objectMapper;

  @Autowired
  public CandidateGroupsCustomHeaderHandler(ObjectMapper objectMapper) {this.objectMapper = objectMapper;}

  @Override
  public Set<String> getHeaderNames() {
    return Set.of("io.camunda.zeebe:candidateGroups");
  }

  @Override
  public void handle(UserTask userTask, String value) throws Exception {
    userTask.setCandidateGroups(objectMapper.readValue(value, STRING_LIST_TYPE));
  }
}
