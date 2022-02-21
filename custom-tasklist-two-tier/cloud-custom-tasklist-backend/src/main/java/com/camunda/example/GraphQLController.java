package com.camunda.example;

import com.camunda.example.model.graphql.GraphQLRequestDto;
import com.camunda.example.model.graphql.GraphQLResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("graphql")
public class GraphQLController {
  private final TasklistClient tasklistClient;

  @Autowired
  public GraphQLController(TasklistClient tasklistClient) {this.tasklistClient = tasklistClient;}

  @PostMapping
  public GraphQLResponseDto<?> graphql(
      @RequestBody GraphQLRequestDto requestDto,
      @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken
  ) {
    return tasklistClient.executeQuery(requestDto, bearerToken);
  }
}
