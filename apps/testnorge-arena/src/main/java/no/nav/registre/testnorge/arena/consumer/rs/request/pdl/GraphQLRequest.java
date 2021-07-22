package no.nav.registre.testnorge.arena.consumer.rs.request.pdl;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class GraphQLRequest {
    String query;
    Map<String, Object> variables;
}
