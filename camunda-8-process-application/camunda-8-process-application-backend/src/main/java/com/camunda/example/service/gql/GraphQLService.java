package com.camunda.example.service.gql;

import com.camunda.example.client.tasklist.*;
import com.camunda.example.client.tasklist.model.*;
import com.fasterxml.jackson.databind.node.*;
import graphql.language.*;
import graphql.parser.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphQLService {

  private final TasklistClient tasklistClient;
  private final Set<ResponseHandler> responseHandlers;
  private final Set<RequestVariableHandler> requestVariableHandlers;

  public GraphQLResponseDto<ObjectNode> executeQuery(GraphQLRequestDto requestDto) throws IOException {
    Document query = Parser.parse(requestDto.getQuery());
    Set<GraphQLOperationDefinition> operations = extractOperationWithVariableNames(query);
    adjustVariables(operations, requestDto.getVariables());
    GraphQLResponseDto<ObjectNode> responseDto = tasklistClient.executeQuery(requestDto);
    adjustResult(operations, responseDto.getData());
    return responseDto;
  }

  private Set<GraphQLOperationDefinition> extractOperationWithVariableNames(Document document) {
    return document
        .getDefinitionsOfType(OperationDefinition.class)
        .stream()
        .flatMap(operationDefinition -> operationDefinition
            .getSelectionSet()
            .getSelectionsOfType(Field.class)
            .stream()
            .map(field -> {
              GraphQLOperationDefinition definition = new GraphQLOperationDefinition();
              definition.setOperation(operationDefinition.getOperation());
              definition.setOperationName(field.getName());
              definition.setVariableMappings(field
                  .getArguments()
                  .stream()
                  .map(argument -> Map.entry(argument.getName(), argument.getValue()))
                  .filter(entry -> VariableReference.class.isAssignableFrom(entry
                      .getValue()
                      .getClass()))
                  .map(entry -> Map.entry(entry.getKey(), VariableReference.class.cast(entry.getValue())))
                  .map(entry -> Map.entry(entry.getKey(),
                      entry
                          .getValue()
                          .getName()))
                  .collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
              definition.setFields(extractFields(field
                  .getSelectionSet()
                  .getSelectionsOfType(Field.class)));
              return definition;
            }))
        .collect(Collectors.toSet());
  }

  private Set<GraphQLOperationField> extractFields(List<Field> fields) {
    return fields
        .stream()
        .map(field -> {
          GraphQLOperationField f = new GraphQLOperationField();
          f.setFieldName(field.getName());
          if (field.getSelectionSet() != null) {
            f.setFields(extractFields(field
                .getSelectionSet()
                .getSelectionsOfType(Field.class)));
          } else {
            f.setFields(Set.of());
          }
          return f;
        })
        .collect(Collectors.toSet());
  }

  private void adjustVariables(Set<GraphQLOperationDefinition> operations, ObjectNode requestVariables) {
    if (requestVariables == null) {
      return;
    }
    operations.forEach(operation -> {
      requestVariableHandlers
          .stream()
          .filter(requestVariableHandler -> requestVariableHandler.canHandle(operation))
          .forEach(requestVariableHandler -> requestVariableHandler.handleRequestVariables(operation,
              requestVariables
          ));
    });
  }

  private void adjustResult(
      Set<GraphQLOperationDefinition> operations, ObjectNode responseData
  ) {
    if (responseData == null) {
      return;
    }
    operations.forEach(operation -> {
      responseHandlers
          .stream()
          .filter(operationHandler -> null != responseData.get(operation.getOperationName()))
          .filter(operationHandler -> operationHandler.canHandle(operation))
          .forEach(operationHandler -> operationHandler.handleResponse(operation,
              responseData.get(operation.getOperationName())
          ));
    });

  }

}
