package com.camunda.example.service.gql;

import com.camunda.example.client.tasklist.model.*;
import com.camunda.example.repository.*;
import com.camunda.example.repository.model.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import graphql.language.OperationDefinition.*;
import lombok.*;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

public abstract class AbstractVariableAppender implements ResponseHandler{
  private static final Map<String, Function<Entry<String, String>, String>> STRING_VARIABLE_FIELD_MAPPERS = Map.of("id",
      entry -> "internal-" + entry.getKey(),
      "name",
      Entry::getKey,
      "value",
      Entry::getValue,
      "previewValue",
      Entry::getValue,
      "__typename",
      entry -> "Variable"
  );
  private static final Map<String, Function<Entry<String, String>, Boolean>> BOOLEAN_VARIABLE_FIELD_MAPPERS = Map.of("isValueTruncated",
      entry -> false
  );
  private final ObjectMapper objectMapper;
  private final InsuranceApplicationRepository insuranceApplicationRepository;
  protected AbstractVariableAppender(ObjectMapper objectMapper,
      InsuranceApplicationRepository insuranceApplicationRepository
  ) {this.objectMapper = objectMapper;
    this.insuranceApplicationRepository = insuranceApplicationRepository;
  }

  @Override
  public boolean canHandle(GraphQLOperationDefinition operationDefinition) {
    return operationDefinition
        .getOperation()
        .equals(Operation.QUERY) && operationDefinition
        .getFields()
        .stream()
        .anyMatch(field -> field
            .getFieldName()
            .equals("variables"));
  }

  @Override
  public void handleResponse(GraphQLOperationDefinition operationDefinition, JsonNode response) {
    Set<String> fieldNames = operationDefinition
        .getFields()
        .stream()
        .filter(field -> field
            .getFieldName()
            .equals("variables"))
        .findFirst()
        .stream()
        .flatMap(field -> field
            .getFields()
            .stream())
        .map(GraphQLOperationField::getFieldName)
        .collect(Collectors.toSet());
    ArrayNode variables = (ArrayNode) response.get("variables");
    StreamSupport
        .stream(variables.spliterator(), false)
        .filter(element -> element.has("name"))
        .filter(element -> element.has("value"))
        .filter(element -> element
            .get("name")
            .asText()
            .equals("applicationId"))
        .findFirst()
        .map(element -> element
            .get("value")
            .asText())
        .map(id -> createAdditionalVariables(id, fieldNames))
        .ifPresent(variables::addAll);
  }

  protected abstract Set<Function<InsuranceApplicationEntity, Entry<String, JsonNode>>> variablesMappers(ObjectMapper objectMapper);

  @SneakyThrows
  private Set<ObjectNode> createAdditionalVariables(String id, Set<String> fieldNames) {
    String applicationId = (objectMapper
        .readTree(id)
        .asText());
    return insuranceApplicationRepository
        .findById(applicationId)
        .map(entity -> variablesMappers(objectMapper)
            .stream()
            .map(func -> func.apply(entity))
            .map(entry -> Map.entry(entry.getKey(),
                entry.getValue().toString()
            ))
            .map(entry -> {
              ObjectNode objectNode = JsonNodeFactory.instance.objectNode();
              fieldNames.forEach(fieldName -> {
                STRING_VARIABLE_FIELD_MAPPERS
                    .entrySet()
                    .stream()
                    .filter(e -> e
                        .getKey()
                        .equals(fieldName))
                    .forEach(e -> objectNode.put(fieldName,
                        e
                            .getValue()
                            .apply(entry)
                    ));
                BOOLEAN_VARIABLE_FIELD_MAPPERS
                    .entrySet()
                    .stream()
                    .filter(e -> e
                        .getKey()
                        .equals(fieldName))
                    .forEach(e -> objectNode.put(fieldName,
                        e
                            .getValue()
                            .apply(entry)
                    ));
              });
              return objectNode;
            })
            .collect(Collectors.toSet()))
        .orElseGet(HashSet::new);
  }
}
