package no.nav.registre.testnorge.person.consumer.dto.graphql;

import lombok.Builder;
import lombok.Value;

import java.util.Map;

@Value
@Builder
public class Request {
    private String query;
    private Map<String, Object> variables;
}
