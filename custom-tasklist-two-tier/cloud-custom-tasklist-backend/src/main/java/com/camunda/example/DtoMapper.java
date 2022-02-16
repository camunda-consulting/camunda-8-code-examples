package com.camunda.example;

import com.camunda.example.model.UserTask;
import com.camunda.example.model.UserTaskDto;
import com.camunda.example.model.graphql.TaskDto;
import com.camunda.example.model.graphql.VariableDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.stream.Collectors;

public class DtoMapper {
  private static final TypeReference<Map<String, Object>> MAP_STRING_OBJECT_TYPE = new TypeReference<Map<String, Object>>() {};

  public static UserTaskDto map(UserTask userTask) {
    UserTaskDto dto = new UserTaskDto();
    dto.setId(userTask.getId());
    dto.setVariables(userTask.getVariables());
    dto.setTaskName(userTask.getTaskName());
    dto.setFormKey(userTask.getFormKey());
    dto.setAssignee(userTask.getAssignee());
    dto.setCandidateGroups(userTask.getCandidateGroups());
    return dto;
  }

  public static UserTask map(UserTaskDto dto) {
    UserTask userTask = new UserTask();
    userTask.setId(dto.getId());
    userTask.setVariables(dto.getVariables());
    userTask.setTaskName(dto.getTaskName());
    userTask.setFormKey(dto.getFormKey());
    userTask.setAssignee(dto.getAssignee());
    userTask.setCandidateGroups(dto.getCandidateGroups());
    return userTask;
  }

  public static UserTask map(TaskDto dto) {
    UserTask userTask = new UserTask();
    userTask.setId(dto.getId());
    userTask.setVariables(dto.getVariables().stream().collect(Collectors.toMap(VariableDto::getName, var -> {
      try {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(var.getValue());
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    })));
    userTask.setTaskName(dto.getName());
    userTask.setFormKey(dto.getFormKey());
    userTask.setAssignee(dto.getAssignee());
    userTask.setCandidateGroups(dto.getCandidateGroups());
    return userTask;
  }
}
