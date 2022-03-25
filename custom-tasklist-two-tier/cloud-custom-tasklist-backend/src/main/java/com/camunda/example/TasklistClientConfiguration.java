package com.camunda.example;

import io.camunda.zeebe.client.CredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import io.camunda.zeebe.spring.client.properties.ZeebeClientConfigurationProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class TasklistClientConfiguration {

  @Bean
  @Qualifier("graphql-url")
  public URI graphQLEndpoint(ZeebeClientConfigurationProperties properties) {
    return URI.create("https://" + properties
        .getCloud()
        .getRegion() + ".tasklist.camunda.io:443/" + properties
        .getCloud()
        .getClusterId() + "/graphql");
  }

  @Bean
  @Qualifier("graphql-credentials-provider")
  public CredentialsProvider graphQLCredentialsProvider(ZeebeClientConfigurationProperties properties){
    return new OAuthCredentialsProviderBuilder()
        .clientId(properties
            .getCloud()
            .getClientId())
        .clientSecret(properties
            .getCloud()
            .getClientSecret())
        .audience("tasklist.camunda.io")
        .build();
  }
}
