package no.nav.registre.orkestratoren.provider.rs;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SyntetiserSkdmeldingerRequest {
    private long skdMeldingGruppeId;
    private String miljoe;
    private Map<String, Integer> antallMeldingerPerAarsakskode;
}
