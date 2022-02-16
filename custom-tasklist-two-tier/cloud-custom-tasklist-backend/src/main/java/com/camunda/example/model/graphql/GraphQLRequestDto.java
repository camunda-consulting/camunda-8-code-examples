package com.camunda.example.model.graphql;

import lombok.Data;

import java.util.Map;

@Data
public class GraphQLRequestDto {
  private String query;
  private Map<String,Object> variables;
}
