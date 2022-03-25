package com.camunda.example;

import com.camunda.example.model.graphql.*;
import com.fasterxml.jackson.databind.*;
import io.camunda.zeebe.client.*;
import io.grpc.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.core.*;
import org.springframework.http.*;
import org.springframework.http.client.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;

import java.io.*;
import java.net.*;
import java.util.*;

@Component
@Slf4j
public class TasklistClient {
  private final RestTemplate restTemplate;
  private final URI graphQLEndpoint;
  private final ObjectMapper objectMapper;
  private final CredentialsProvider credentialsProvider;

  @Autowired
  public TasklistClient(
      ObjectMapper objectMapper,
      @Qualifier("graphql-url") URI graphQLEndpoint,
      @Qualifier("graphql-credentials-provider") CredentialsProvider credentialsProvider
  ) {
    this.objectMapper = objectMapper;
    this.restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    this.graphQLEndpoint = graphQLEndpoint;
    this.credentialsProvider = credentialsProvider;
  }

  private <T extends GraphQLResponseDto<?>> T executeQuery(
      GraphQLRequestDto requestDto, ParameterizedTypeReference<T> typeReference, Optional<String> bearerToken
  ) {
    logAsJson("Requesting from tasklist: \n{}", requestDto);

    HttpHeaders headers = createHeaders(bearerToken);

    HttpEntity<GraphQLRequestDto> graphQLRequestEntity = new HttpEntity<>(requestDto, headers);
    ResponseEntity<T> entity = restTemplate.exchange(graphQLEndpoint,
        HttpMethod.POST,
        graphQLRequestEntity,
        typeReference
    );
    T body = entity.getBody();
    logAsJson("Response from tasklist: \n{}", body);
    return body;
  }

  public GraphQLResponseDto<JsonNode> executeQuery(GraphQLRequestDto dto, Optional<String> bearerToken) {
    return executeQuery(dto, new ParameterizedTypeReference<>() {}, bearerToken);
  }

  private void logAsJson(String message, Object object) {
    try {
      log.info(message,
          objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(object)
      );
    } catch (Exception e) {
      log.info(message, object);
    }
  }

  private HttpHeaders createHeaders(Optional<String> bearerToken) {
    Metadata metadata = new Metadata();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    bearerToken.ifPresentOrElse((token) -> headers.setBearerAuth(token.replace("Bearer ", "")), () -> {
      try {
        credentialsProvider.applyCredentials(metadata);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    metadata
        .keys()
        .forEach(key -> {
          metadata
              .getAll(Metadata.Key.of(key, Metadata.ASCII_STRING_MARSHALLER))
              .forEach(value -> {
                log.info("Reading metadata: {} = {}", key, value);
                headers.add(key, value);
              });
        });
    return headers;
  }

}
