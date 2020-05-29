package no.nav.registre.spion.provider.rs.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.spion.domain.Vedtak;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class SyntetiserVedtakResponse {

    private final String identitetsnummer;
    private final List<Vedtak> vedtak;

}

