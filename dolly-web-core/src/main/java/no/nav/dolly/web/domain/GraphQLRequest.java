package no.nav.dolly.web.domain;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GraphQLRequest {
    private String query;
    private Map<String, Object> variables;
}