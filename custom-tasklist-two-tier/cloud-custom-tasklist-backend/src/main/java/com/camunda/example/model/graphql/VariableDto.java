package com.camunda.example.model.graphql;

import lombok.Data;

@Data
public class VariableDto {
  private String id;
  private String name;
  private String value;
  private String previewValue;
  private Boolean isValueTruncated;
}
