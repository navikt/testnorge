package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pdl;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}
