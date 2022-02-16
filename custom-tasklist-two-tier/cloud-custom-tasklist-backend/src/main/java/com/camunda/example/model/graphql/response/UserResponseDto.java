package com.camunda.example.model.graphql.response;

import com.camunda.example.model.graphql.UserDto;
import lombok.Data;

@Data
public class UserResponseDto {
  private UserDto currentUser;
}
