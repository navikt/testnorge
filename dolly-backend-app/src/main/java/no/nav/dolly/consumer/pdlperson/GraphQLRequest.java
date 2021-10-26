package no.nav.dolly.consumer.pdlperson;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}