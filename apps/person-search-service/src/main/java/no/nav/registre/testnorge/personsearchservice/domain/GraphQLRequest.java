package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.Builder;

import java.util.Map;

@Builder
public record GraphQLRequest(String query, Criterion ) {
}