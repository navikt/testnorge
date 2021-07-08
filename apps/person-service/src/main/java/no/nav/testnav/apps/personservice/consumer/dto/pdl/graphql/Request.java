package no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql;

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
