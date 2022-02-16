package com.camunda.example;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphQLBuilder {
  private String verb;
  private String queryName;
  private Set<VariableInput> variableInputs;
  private String queryDefinitionName;
  private Set<QueryVariable> queryVariables;
  private Set<QueryField> queryFields;

  private static Set<String> build(Set<? extends Buildable> buildables, int indent) {
    return buildables.stream().map(buildable -> buildable.build(indent)).collect(Collectors.toSet());
  }

  private static String indent(int indent) {
    return " ".repeat(Math.max(0, indent));
  }

  public static GraphQLBuilder mutation(String name) {
    GraphQLBuilder builder = new GraphQLBuilder();
    builder.queryName = name;
    builder.verb = "mutation";
    return builder;
  }

  public static GraphQLBuilder query(String name) {
    GraphQLBuilder builder = new GraphQLBuilder();
    builder.queryName = name;
    builder.verb = "query";
    return builder;
  }

  public static VariableInputBuilder input() {
    return new VariableInputBuilder();
  }

  public static QueryVariableBuilder queryVars() {
    return new QueryVariableBuilder();
  }

  public static QueryDefinitionBuilder queryDef() {
    return new QueryDefinitionBuilder();
  }

  public GraphQLBuilder queryDefinitionName(String queryDefinitionName) {
    this.queryDefinitionName = queryDefinitionName;
    return this;
  }

  public GraphQLBuilder variableInput(VariableInputBuilder variableInputBuilder) {
    this.variableInputs = variableInputBuilder.variableInputs;
    return this;
  }

  public GraphQLBuilder queryVariables(QueryVariableBuilder queryVariableBuilder) {
    this.queryVariables = queryVariableBuilder.queryVariables;
    return this;
  }

  public GraphQLBuilder queryDefinition(QueryDefinitionBuilder queryDefinitionBuilder) {
    this.queryFields = queryDefinitionBuilder.queryFields;
    return this;
  }

  public String build() {
    return buildQueryNameAndInputs() + " {\n  " + buildQueryDefinitionNameAndVariables() + " {\n" + buildQuery() + "\n  }\n}";
  }

  private String buildQueryNameAndInputs() {
    String queryNameAndInputs = verb + " " + queryName;
    if (variableInputs != null && !variableInputs.isEmpty()) {
      queryNameAndInputs += "(\n" + String.join("\n", build(variableInputs, 2)) + "\n)";
    }
    return queryNameAndInputs;
  }

  private String buildQueryDefinitionNameAndVariables() {
    String queryDefinitionNameAndVariables = queryDefinitionName;
    if (queryVariables != null && !queryVariables.isEmpty()) {
      queryDefinitionNameAndVariables += "(\n" + String.join("\n", build(queryVariables, 4)) + "\n  )";
    }
    return queryDefinitionNameAndVariables;
  }

  private String buildQuery() {
    return String.join("\n", GraphQLBuilder.build(queryFields, 4));
  }

  private interface Buildable {
    String build(int indent);
  }

  public static class VariableInputBuilder {
    private Set<VariableInput> variableInputs = new HashSet<>();

    public VariableInputBuilder variable(String variableName, String variableType) {
      VariableInput input = new VariableInput();
      input.variableName = variableName;
      input.variableType = variableType;
      variableInputs.add(input);
      return this;
    }
  }

  public static class VariableInput implements Buildable {
    private String variableName;
    private String variableType;

    @Override
    public String build(int indent) {
      return indent(indent) + "$" + variableName + ": " + variableType;
    }
  }

  public static class QueryVariableBuilder {
    private final Set<QueryVariable> queryVariables = new HashSet<>();

    public QueryVariableBuilder inputVariable(String queryVariable, String inputVariableName) {
      InputQueryVariable inputQueryVariable = new InputQueryVariable();
      inputQueryVariable.variableName = queryVariable;
      inputQueryVariable.inputVariableName = inputVariableName;
      queryVariables.add(inputQueryVariable);
      return this;
    }
  }

  public static abstract class QueryVariable implements Buildable {
    protected String variableName;
  }

  public static class InputQueryVariable extends QueryVariable {
    private String inputVariableName;

    @Override
    public String build(int indent) {
      return indent(indent) + variableName + ": $" + inputVariableName;
    }
  }

  public static class ObjectQueryVariable extends QueryVariable {
    private Set<QueryVariable> queryVariables;

    @Override
    public String build(int indent) {
      return variableName + ": {" + String.join(" ", GraphQLBuilder.build(queryVariables, indent + 2));
    }
  }

  public static class ArrayQueryVariable extends QueryVariable {
    private Set<String> inputVariableNames;

    @Override
    public String build(int indent) {
      return variableName + ": [" + String.join(" ",
          inputVariableNames.stream().map(name -> "$" + name).collect(Collectors.toSet())
      ) + "]";
    }
  }

  public static class QueryDefinitionBuilder {
    private final Set<QueryField> queryFields = new HashSet<>();

    public QueryDefinitionBuilder scalarField(String fieldName) {
      ScalarQueryField scalarQueryField = new ScalarQueryField();
      scalarQueryField.fieldName = fieldName;
      queryFields.add(scalarQueryField);
      return this;
    }

    public QueryDefinitionBuilder typeField(String fieldName, QueryDefinitionBuilder builder) {
      TypeQueryField typeQueryField = new TypeQueryField();
      typeQueryField.fieldName = fieldName;
      typeQueryField.queryFields = builder.queryFields;
      queryFields.add(typeQueryField);
      return this;
    }
  }

  public static abstract class QueryField implements Buildable {
    protected String fieldName;
  }

  public static class ScalarQueryField extends QueryField {

    @Override
    public String build(int indent) {
      return indent(indent) + fieldName;
    }
  }

  public static class TypeQueryField extends QueryField {
    private Set<QueryField> queryFields;

    @Override
    public String build(int indent) {
      if (queryFields == null || queryFields.isEmpty()) {
        return fieldName;
      }
      return indent(indent) + fieldName + " {\n" + String.join("\n",
          GraphQLBuilder.build(queryFields, indent + 2)
      ) + "\n" + indent(indent) + "}";
    }
  }
}
