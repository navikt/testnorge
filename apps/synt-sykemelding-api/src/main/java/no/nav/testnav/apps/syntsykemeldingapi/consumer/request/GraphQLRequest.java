package no.nav.testnav.apps.syntsykemeldingapi.consumer.request;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}
