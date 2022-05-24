package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentGeografiskTilknytningModal {
    private String gtType;
    private String gtLand;
    private String gtKommune;
    private String gtBydel;
    private String regel;
}
