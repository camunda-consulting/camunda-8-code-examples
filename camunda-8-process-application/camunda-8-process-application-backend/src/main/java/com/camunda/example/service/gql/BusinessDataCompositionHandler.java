package com.camunda.example.service.gql;

import com.camunda.example.client.tasklist.model.*;
import com.camunda.example.repository.*;
import com.camunda.example.repository.model.*;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.Map.*;
import java.util.function.*;
import java.util.stream.*;

@Component
@Slf4j
public class BusinessDataCompositionHandler extends AbstractVariableAppender {
  public BusinessDataCompositionHandler(
      ObjectMapper objectMapper, InsuranceApplicationRepository insuranceApplicationRepository
  ) {
    super(objectMapper, insuranceApplicationRepository);
  }

  @Override
  protected Set<Function<InsuranceApplicationEntity, Entry<String, JsonNode>>> variablesMappers(ObjectMapper objectMapper) {
    return Set.of(entity -> Map.entry("applicantName",
        objectMapper.valueToTree(entity.getApplicantName())
    ));
  }


}
