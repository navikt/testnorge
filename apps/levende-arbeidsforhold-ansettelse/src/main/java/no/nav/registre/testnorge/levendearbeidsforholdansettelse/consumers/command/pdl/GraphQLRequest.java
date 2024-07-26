package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {
}