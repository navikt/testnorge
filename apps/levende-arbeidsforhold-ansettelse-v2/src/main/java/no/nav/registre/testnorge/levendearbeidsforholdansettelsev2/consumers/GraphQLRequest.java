package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}