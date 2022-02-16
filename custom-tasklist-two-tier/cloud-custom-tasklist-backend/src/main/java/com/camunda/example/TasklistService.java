package com.camunda.example;

import com.camunda.example.model.UserTask;
import com.camunda.example.model.graphql.TaskQueryDto;
import com.camunda.example.model.graphql.TaskStateEnum;
import com.camunda.example.model.graphql.VariableInputDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TasklistService {
  private static final Logger LOG = LoggerFactory.getLogger(TasklistService.class);
  private final TasklistClient tasklistClient;

  @Autowired
  public TasklistService(TasklistClient tasklistClient) {
    this.tasklistClient = tasklistClient;
  }

  public Stream<UserTask> stream() {
    TaskQueryDto query = new TaskQueryDto();
    query.setState(TaskStateEnum.CREATED);
    return tasklistClient
        .getTasks(query)
        .stream()
        .map(DtoMapper::map);
  }

  public UserTask complete(UserTask userTask, Map<String, JsonNode> variables) {
    return Optional
        .of(tasklistClient.completeTask(
            userTask.getId(),
            variables
                .entrySet()
                .stream()
                .filter(e -> !e
                    .getValue()
                    .equals(userTask
                        .getVariables()
                        .get(e.getKey())))
                .map(e -> {
                  VariableInputDto dto = new VariableInputDto();
                  dto.setName(e.getKey());
                  dto.setValue(e
                      .getValue()
                      .toString());
                  return dto;
                })
                .collect(Collectors.toList())
        ))
        .map(DtoMapper::map)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  public Optional<UserTask> get(String taskId) {
    return Optional
        .ofNullable(tasklistClient.getTask(taskId))
        .map(DtoMapper::map);
  }

  public UserTask claim(UserTask userTask, String userId) {
    return Optional
        .of(tasklistClient.claimTask(userTask.getId(), userId))
        .map(DtoMapper::map)
        .get();
  }

  public UserTask unclaim(UserTask userTask) {
    return Optional
        .of(tasklistClient.unclaimTask(userTask.getId()))
        .map(DtoMapper::map)
        .get();
  }
}
