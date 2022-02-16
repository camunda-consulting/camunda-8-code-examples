package com.camunda.example.model.graphql;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TaskQueryDto {
  private TaskStateEnum state;
  private Boolean assigned;
  private String assignee;
  private String candidateGroup;
  private Integer pageSize;
  private String taskDefinitionId;
  private List<String> searchAfter;
  private List<String> searchAfterOrEqual;
  private List<String> searchBefore;
  private List<String> searchBeforeOrEqual;
}
