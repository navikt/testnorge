package no.nav.dolly.bestilling.aareg.domain;

import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AaregResponse {

    private Map<String, String> statusPerMiljoe;

    public Map<String, String> getStatusPerMiljoe() {
        if (isNull(statusPerMiljoe)) {
            statusPerMiljoe = new HashMap<>();
        }
        return statusPerMiljoe;
    }
}
