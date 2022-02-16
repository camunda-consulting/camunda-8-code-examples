package com.camunda.example.model.graphql;

import com.camunda.example.GraphQLBuilder;
import lombok.Data;

import java.util.List;

import static com.camunda.example.GraphQLBuilder.*;

@Data
public class TaskDto {
  private String id;
  private String name;
  private String taskDefinitionId;
  private String processName;
  private String creationTime;
  private String completionTime;
  private String assignee;
  private List<VariableDto> variables;
  private TaskStateEnum taskState;
  private List<String> sortValues;
  private Boolean isFirst;
  private String formKey;
  private String processDefinitionId;
  private List<String> candidateGroups;

  public static GraphQLBuilder.QueryDefinitionBuilder queryDef(){
    return GraphQLBuilder.queryDef().scalarField("id")
        .scalarField("name")
        .scalarField("taskDefinitionId")
        .scalarField("processName")
        .scalarField("creationTime")
        .scalarField("completionTime")
        .scalarField("assignee")
        .typeField("variables",
            GraphQLBuilder.queryDef().scalarField("id")
                .scalarField("name")
                .scalarField("value")
                .scalarField("previewValue")
                .scalarField("isValueTruncated")
        )
        .scalarField("taskState")
        .scalarField("sortValues")
        .scalarField("isFirst")
        .scalarField("formKey")
        .scalarField("processDefinitionId")
        .scalarField("candidateGroups");
  }
}
