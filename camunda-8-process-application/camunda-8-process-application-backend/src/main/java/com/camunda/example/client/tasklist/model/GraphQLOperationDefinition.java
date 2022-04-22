package com.camunda.example.client.tasklist.model;

import graphql.language.OperationDefinition.*;
import lombok.*;

import java.util.*;

@Data
public class GraphQLOperationDefinition {
  private Operation operation;
  private String operationName;
  private Map<String,String> variableMappings;
  private Set<GraphQLOperationField> fields;
}
