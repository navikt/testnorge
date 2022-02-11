package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GraphQLRequest {
    String query;
    Map<String, Object> variables;
}
