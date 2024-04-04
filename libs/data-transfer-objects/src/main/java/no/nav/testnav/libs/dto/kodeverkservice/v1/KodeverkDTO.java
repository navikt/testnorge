package no.nav.testnav.libs.dto.kodeverkservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeverkDTO {

    private String kodeverknavn;
    private Map<String, String> kodeverk;

    public Map<String, String> getKodeverk() {
        
        if (isNull(kodeverk)) {
            kodeverk = new HashMap<>();
        }
        return kodeverk;
    }
}
