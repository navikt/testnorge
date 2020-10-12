package no.nav.registre.skd.consumer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.skd.consumer.dto.Relasjon;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonsResponse {

    String fnr;
    List<Relasjon> relasjoner;

}
