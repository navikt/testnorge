package no.nav.testnav.apps.personservice.consumer.v2;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}