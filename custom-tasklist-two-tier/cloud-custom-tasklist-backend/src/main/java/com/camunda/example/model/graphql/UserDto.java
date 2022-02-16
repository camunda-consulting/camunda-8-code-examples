package com.camunda.example.model.graphql;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {
  private String username;
  private String firstname;
  private String lastname;
  private Set<String> permissions;
}
