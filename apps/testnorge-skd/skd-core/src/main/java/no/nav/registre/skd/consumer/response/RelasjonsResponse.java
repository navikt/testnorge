package no.nav.registre.skd.consumer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.registre.skd.consumer.dto.Relasjon;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonsResponse {

    private final String fnr;
    private final List<Relasjon> relasjoner;

}
