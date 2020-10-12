package no.nav.registre.skd.consumer.response;

import lombok.Builder;
import lombok.Data;
import no.nav.registre.skd.consumer.dto.Relasjon;

import java.util.List;

@Data
@Builder
public class RelasjonsResponse {

    private final String fnr;
    private final List<Relasjon> relasjoner;

    public RelasjonsResponse(String fnr, List<Relasjon> relasjoner) {
        this.fnr = fnr;
        this.relasjoner = relasjoner;
    }
}
