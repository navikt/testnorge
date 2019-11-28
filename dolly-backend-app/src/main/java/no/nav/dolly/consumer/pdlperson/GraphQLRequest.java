package no.nav.dolly.consumer.pdlperson;

import java.util.Map;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GraphQLRequest {
    private String query;
    private Map<String, Object> variables;
}