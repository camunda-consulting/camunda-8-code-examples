package com.camunda.example.model.graphql.response;

import com.camunda.example.model.graphql.TaskDto;
import lombok.Data;

@Data
public class CompleteTaskResponseDto {
  private TaskDto completeTask;
}