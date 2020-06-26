package no.nav.registre.testnorge.person.consumer.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Request {
    private String query;
    private Map<String, Object> variables;
}
