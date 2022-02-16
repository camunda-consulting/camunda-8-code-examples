package com.camunda.example.model;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserTask {
  private String id;
  private String assignee;
  private List<String> candidateGroups;
  private String formKey;
  private String taskName;
  private Map<String, JsonNode> variables;
}
