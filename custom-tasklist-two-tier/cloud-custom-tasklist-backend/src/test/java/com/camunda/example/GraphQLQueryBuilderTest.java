package com.camunda.example;

import org.junit.jupiter.api.Test;

import static com.camunda.example.GraphQLBuilder.*;

public class GraphQLQueryBuilderTest {

  @Test
  public void shouldCreateQuery() {
    String build = query("getTasks").queryDefinitionName("tasks")
        .variableInput(input().variable("query", "TaskQuery"))
        .queryVariables(queryVars().inputVariable("query", "query"))
        .queryDefinition(queryDef().scalarField("ID")
            .scalarField("name")
            .scalarField("taskDefinitionId")
            .scalarField("processName")
            .scalarField("creationTime")
            .scalarField("completionTime")
            .scalarField("assignee")
            .typeField(
                "variables",
                queryDef().scalarField("id")
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
            .scalarField("candidateGroups"))
        .build();
    System.out.println(build);
  }
}
