package com.camunda.example;

import com.camunda.example.model.graphql.GraphQLRequestDto;
import com.camunda.example.model.graphql.GraphQLResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.camunda.zeebe.client.impl.ZeebeClientCredentials;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class TasklistClient {
  private final RestTemplate restTemplate;
  private final ZeebeClientConfigurationProperties properties;
  private final String graphQLEndpoint;
  private final ObjectMapper objectMapper;
  private ZeebeClientCredentials credentials;

  @Autowired
  public TasklistClient(
      ZeebeClientConfigurationProperties properties, ObjectMapper objectMapper
  ) {
    this.objectMapper = objectMapper;
    this.restTemplate = new RestTemplate();
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    this.properties = properties;
    this.graphQLEndpoint = "https://" + properties
        .getCloud()
        .getRegion() + ".tasklist.camunda.io:443/" + properties
        .getCloud()
        .getClusterId() + "/graphql";
  }

  private String getAccessToken() {
    if (credentials == null || !credentials.isValid()) {
      createCredentials();
    }
    return credentials.getAccessToken();
  }

  private void createCredentials() {
    Map<String, String> body = new HashMap<>();
    body.put("client_id",
        properties
            .getCloud()
            .getClientId()
    );
    body.put("client_secret",
        properties
            .getCloud()
            .getClientSecret()
    );
    body.put("audience", "tasklist.camunda.io");
    body.put("grant_type", "client_credentials");
    ResponseEntity<ZeebeClientCredentials> response = restTemplate.postForEntity(properties
        .getCloud()
        .getAuthUrl(), body, ZeebeClientCredentials.class);
    this.credentials = response.getBody();
  }

  private <T extends GraphQLResponseDto<?>> T executeQuery(
      GraphQLRequestDto requestDto, ParameterizedTypeReference<T> typeReference, Optional<String> bearerToken
  ) {
    try {
      log.info("Requesting from tasklist: \n{}",
          objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(requestDto)
      );
    } catch (Exception e) {
      log.info("Requesting from tasklist: \n{}", requestDto);
    }
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    bearerToken.ifPresentOrElse((token) -> headers.setBearerAuth(token.replace("Bearer ", "")),
        () -> headers.setBearerAuth(getAccessToken()));
    HttpEntity<GraphQLRequestDto> graphQLRequestEntity = new HttpEntity<>(requestDto, headers);
    ResponseEntity<T> entity = restTemplate.exchange(URI.create(graphQLEndpoint),
        HttpMethod.POST,
        graphQLRequestEntity,
        typeReference
    );
    try {
      log.info("Response from tasklist: \n{}",
          objectMapper
              .writerWithDefaultPrettyPrinter()
              .writeValueAsString(entity.getBody())
      );
    } catch (Exception e) {
      log.info("with Variables: \n{}", entity.getBody());
    }
    return entity.getBody();
  }

  public GraphQLResponseDto<?> executeQuery(GraphQLRequestDto dto, Optional<String> bearerToken) {
    return executeQuery(dto, new ParameterizedTypeReference<>() {}, bearerToken);
  }

}
