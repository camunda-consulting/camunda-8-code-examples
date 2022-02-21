package com.camunda.example;

import com.camunda.example.model.graphql.*;
import com.camunda.example.model.graphql.response.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.impl.ZeebeClientCredentials;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.camunda.example.GraphQLBuilder.*;

@Component
@Slf4j
public class TasklistClient {
  private final RestTemplate restTemplate;
  private final ZeebeClientConfigurationProperties properties;
  private final String graphQLEndpoint;
  private final ObjectMapper objectMapper;
  private ZeebeClientCredentials credentials;

  @Autowired
  public TasklistClient(
      ZeebeClientConfigurationProperties properties, ObjectMapper objectMapper
  ) {
    this.objectMapper = objectMapper;
    this.restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    this.properties = properties;
    this.graphQLEndpoint = "https://" + properties
        .getCloud()
        .getRegion() + ".tasklist.camunda.io:443/" + properties
        .getCloud()
        .getClusterId() + "/graphql";
  }

  private String getAccessToken() {
    if (credentials == null || !credentials.isValid()) {
      createCredentials();
    }
    return credentials.getAccessToken();
  }

  private void createCredentials() {
    Map<String, String> body = new HashMap<>();
    body.put("client_id",
        properties
            .getCloud()
            .getClientId()
    );
    body.put("client_secret",
        properties
            .getCloud()
            .getClientSecret()
    );
    body.put("audience", "tasklist.camunda.io");
    body.put("grant_type", "client_credentials");
    ResponseEntity<ZeebeClientCredentials> response = restTemplate.postForEntity(properties
        .getCloud()
        .getAuthUrl(), body, ZeebeClientCredentials.class);
    this.credentials = response.getBody();
  }

  public List<TaskDto> getTasks(TaskQueryDto taskQuery) {
    GraphQLRequestDto requestDto = new GraphQLRequestDto();
    requestDto.setQuery(query("getTasks")
        .queryDefinitionName("tasks")
        .variableInput(input().variable("query", "TaskQuery!"))
        .queryVariables(queryVars().inputVariable("query", "query"))
        .queryDefinition(TaskDto.queryDef())
        .build());
    requestDto.setVariables(Map.of("query", taskQuery));
    return executeQuery(requestDto, new ParameterizedTypeReference<GraphQLResponseDto<TasksResponseDto>>() {})
        .getData()
        .getTasks();
  }

  public TaskDto getTask(String taskId) {
    GraphQLRequestDto requestDto = new GraphQLRequestDto();
    requestDto.setQuery(query("getTask")
        .queryDefinitionName("task")
        .variableInput(input().variable("taskId", "String!"))
        .queryVariables(queryVars().inputVariable("id", "taskId"))
        .queryDefinition(TaskDto.queryDef())
        .build());
    requestDto.setVariables(Map.of("taskId", taskId));
    return executeQuery(requestDto, new ParameterizedTypeReference<GraphQLResponseDto<TaskResponseDto>>() {})
        .getData()
        .getTask();
  }

  public TaskDto completeTask(String taskId, List<VariableInputDto> variables) {
    GraphQLRequestDto requestDto = new GraphQLRequestDto();
    requestDto.setQuery(mutation("completeTask")
        .queryDefinitionName("completeTask")
        .variableInput(input()
            .variable("taskId", "String!")
            .variable("variables", "[VariableInput!]!"))
        .queryVariables(queryVars()
            .inputVariable("taskId", "taskId")
            .inputVariable("variables", "variables"))
        .queryDefinition(TaskDto.queryDef())
        .build());
    requestDto.setVariables(Map.of("taskId", taskId, "variables", variables));
    return executeQuery(requestDto, new ParameterizedTypeReference<GraphQLResponseDto<CompleteTaskResponseDto>>() {})
        .getData()
        .getCompleteTask();

  }

  public UserDto getCurrentUser() {
    GraphQLRequestDto requestDto = new GraphQLRequestDto();
    requestDto.setQuery(query("getCurrentUser")
        .queryDefinitionName("currentUser")
        .queryDefinition(queryDef()
            .scalarField("username")
            .scalarField("firstname")
            .scalarField("lastname")
            .scalarField("permissions"))
        .build());
    return executeQuery(requestDto, new ParameterizedTypeReference<GraphQLResponseDto<UserResponseDto>>() {})
        .getData()
        .getCurrentUser();
  }

  private <T extends GraphQLResponseDto<?>> T executeQuery(
      GraphQLRequestDto requestDto, ParameterizedTypeReference<T> typeReference
  ) {
    try {
      log.info("Requesting from tasklist: \n{}",
          objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(requestDto)
      );
    } catch (Exception e) {
      log.info("Requesting from tasklist: \n{}", requestDto);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(getAccessToken());
    HttpEntity<GraphQLRequestDto> graphQLRequestEntity = new HttpEntity<>(requestDto, headers);
    ResponseEntity<T> entity = restTemplate.exchange(URI.create(graphQLEndpoint),
        HttpMethod.POST,
        graphQLRequestEntity,
        typeReference
    );
    try {
      log.info(
          "Response from tasklist: \n{}",
          objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(entity.getBody())
      );
    } catch (Exception e) {
      log.info("with Variables: \n{}", entity.getBody());
    }
    return entity.getBody();
  }

  public GraphQLResponseDto<?> executeQuery(GraphQLRequestDto dto, Optional<String> bearerToken) {
    return executeQuery(dto, new ParameterizedTypeReference<>() {});
  }

  public TaskDto claimTask(String taskId, String userId) {
    GraphQLRequestDto requestDto = new GraphQLRequestDto();
    requestDto.setQuery(mutation("claimTask")
        .variableInput(input()
            .variable("taskId", "String!")
            .variable("userId", "String"))
        .queryDefinitionName("claimTask")
        .queryVariables(queryVars()
            .inputVariable("taskId", "taskId")
            .inputVariable("assignee", "userId"))
        .queryDefinition(TaskDto.queryDef())
        .build());
    requestDto.setVariables(Map.of("userId", userId, "taskId", taskId));
    return executeQuery(requestDto, new ParameterizedTypeReference<GraphQLResponseDto<ClaimTaskResponseDto>>() {})
        .getData()
        .getClaimTask();
  }

  public TaskDto unclaimTask(String taskId) {
    GraphQLRequestDto requestDto = new GraphQLRequestDto();
    requestDto.setQuery(mutation("unclaimTask")
        .variableInput(input().variable("taskId", "String!"))
        .queryDefinitionName("unclaimTask")
        .queryVariables(queryVars().inputVariable("taskId", "taskId"))
        .queryDefinition(TaskDto.queryDef())
        .build());
    requestDto.setVariables(Map.of("taskId", taskId));
    return executeQuery(requestDto, new ParameterizedTypeReference<GraphQLResponseDto<UnclaimTaskResponseDto>>() {})
        .getData()
        .getUnclaimTask();
  }
}
