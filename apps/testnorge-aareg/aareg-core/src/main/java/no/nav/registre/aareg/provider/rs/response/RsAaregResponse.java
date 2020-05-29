package no.nav.registre.aareg.provider.rs.response;

import static java.util.Objects.isNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

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
