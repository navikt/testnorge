package no.nav.registre.testnorge.arena.consumer.rs.response.aktoer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.testnorge.arena.consumer.rs.response.aktoer.AktoerInnhold;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AktoerResponse {

    private List<AktoerInnhold> identer;
    private String feilmelding;
}
