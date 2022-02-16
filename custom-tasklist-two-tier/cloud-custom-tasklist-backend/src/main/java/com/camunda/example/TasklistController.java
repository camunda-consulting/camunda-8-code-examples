package com.camunda.example;

import com.camunda.example.model.ClaimDto;
import com.camunda.example.model.UserTaskDto;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("task")
@Slf4j
public class TasklistController {
  private final TasklistService tasklistService;

  public TasklistController(TasklistService tasklistService) {this.tasklistService = tasklistService;}

  @GetMapping
  public List<UserTaskDto> list() {
    log.info("Returning list");
    return tasklistService
        .stream()
        .map(DtoMapper::map)
        .collect(Collectors.toList());
  }

  @GetMapping("{taskId}")
  public UserTaskDto get(@PathVariable String taskId) {
    return tasklistService
        .get(taskId)
        .map(DtoMapper::map)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  @PostMapping("{taskId}/complete")
  public UserTaskDto complete(@PathVariable String taskId, @RequestBody Map<String, JsonNode> variables) {
    return tasklistService
        .get(taskId)
        .map(userTask -> tasklistService.complete(userTask,variables))
        .map(DtoMapper::map)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  @PostMapping("{taskId}/claim")
  public UserTaskDto claim(
      @PathVariable String taskId, @RequestBody ClaimDto claimDto, @RequestHeader("Authorization") String userToken
  ) {
    return tasklistService
        .get(taskId)
        .map(userTask -> tasklistService.claim(userTask,claimDto.getUserId()))
        .map(DtoMapper::map)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  @PostMapping("{taskId}/unclaim")
  public UserTaskDto claim(@PathVariable String taskId) {
    return tasklistService
        .get(taskId)
        .map(tasklistService::unclaim)
        .map(DtoMapper::map)
        .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }
}
