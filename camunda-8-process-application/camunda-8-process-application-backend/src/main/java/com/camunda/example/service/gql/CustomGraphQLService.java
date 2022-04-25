package com.camunda.example.service.gql;

import com.camunda.example.client.tasklist.model.*;
import com.fasterxml.jackson.core.type.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import graphql.*;
import graphql.language.OperationDefinition.*;
import lombok.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.function.*;

@Service
@RequiredArgsConstructor
public class CustomGraphQLService {
  private static final Map<Operation, BiPredicate<GraphQL, String>> HANDLE_PREDICATES = Map.of(Operation.QUERY,
      (gql, operationName) -> gql
          .getGraphQLSchema()
          .getQueryType()
          .getField(operationName) != null,
      Operation.MUTATION,
      (gql, operationName) -> gql
          .getGraphQLSchema()
          .getMutationType()
          .getField(operationName) != null,
      Operation.SUBSCRIPTION,
      (gql, operationName) -> gql
          .getGraphQLSchema()
          .getSubscriptionType()
          .getField(operationName) != null
  );
  private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() {};
  private static final TypeReference<GraphQLResponseDto<ObjectNode>> RESPONSE_TYPE_REFERENCE = new TypeReference<>() {};
  private final GraphQL graphQL;
  private final ObjectMapper objectMapper;

  public GraphQLResponseDto<ObjectNode> execute(GraphQLRequestDto requestDto) {
    return objectMapper.convertValue(graphQL.execute(createInput(requestDto)), RESPONSE_TYPE_REFERENCE);
  }

  public boolean canExecute(GraphQLOperationDefinition operationDefinition) {
    return HANDLE_PREDICATES
        .get(operationDefinition.getOperationDefinitionType())
        .test(graphQL, operationDefinition.getOperationName());
  }

  private ExecutionInput createInput(GraphQLRequestDto requestDto) {
    return ExecutionInput
        .newExecutionInput()
        .query(requestDto.getQuery())
        .graphQLContext(objectMapper.convertValue(requestDto.getVariables(),
            MAP_TYPE_REFERENCE))
        .variables(objectMapper.convertValue(requestDto.getVariables(), MAP_TYPE_REFERENCE))
        .build();
  }
}
