package com.camunda.example.service.gql;

import com.camunda.example.client.operate.*;
import com.camunda.example.service.gql.model.*;
import graphql.*;
import graphql.schema.*;
import graphql.schema.idl.*;
import org.springframework.context.annotation.*;

import java.io.*;

import static graphql.schema.idl.RuntimeWiring.*;

@Configuration
public class GraphQLServiceConfiguration {

  @Bean
  public GraphQL graphQL(OperateClient operateClient) throws IOException {
    TypeDefinitionRegistry typeDefinitionRegistry = getSchema();
    RuntimeWiring runtimeWiring = newRuntimeWiring()
        .type("Query", typeWiring -> typeWiring.dataFetcher("bpmnXml", environment -> {
          Long processDefinitionId = Long.parseLong(environment.getArgument("processDefinitionId"));
          GqlBpmnXmlDto dto = new GqlBpmnXmlDto();
          dto.setId(processDefinitionId);
          dto.setData(operateClient
              .getProcessDefinitionsEndpoint()
              .xml(processDefinitionId));
          return dto;
        }))
        .build();

    SchemaGenerator schemaGenerator = new SchemaGenerator();
    GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);
    return GraphQL
        .newGraphQL(graphQLSchema)
        .build();
  }

  private TypeDefinitionRegistry getSchema() throws IOException {
    try (
        InputStream schema = getClass()
            .getClassLoader()
            .getResourceAsStream("schema.graphqls")
    ) {
      SchemaParser schemaParser = new SchemaParser();
      return schemaParser.parse(schema);
    }
  }
}
