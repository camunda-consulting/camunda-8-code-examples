package com.camunda.example.model.graphql;

import lombok.Data;

import java.util.List;

@Data
public class TasksResponseDto {
  private List<TaskDto> tasks;
}
