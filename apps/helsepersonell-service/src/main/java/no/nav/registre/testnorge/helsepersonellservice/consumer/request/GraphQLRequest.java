package no.nav.registre.testnorge.helsepersonellservice.consumer.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}
