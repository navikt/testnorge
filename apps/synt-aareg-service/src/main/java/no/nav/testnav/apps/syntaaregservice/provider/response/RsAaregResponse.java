package no.nav.testnav.apps.syntaaregservice.provider.response;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsAaregResponse {

    private Map<String, String> statusPerMiljoe;

    public Map<String, String> getStatusPerMiljoe() {
        if (isNull(statusPerMiljoe)) {
            statusPerMiljoe = new HashMap<>();
        }
        return statusPerMiljoe;
    }
}
