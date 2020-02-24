package no.nav.registre.spion.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Vedtak {

    private final String identitetsnummer;
    private final String virksomhetsnummer;
    private final String fom;
    private final String tom;
    private final String ytelse;
    private final String vedtaksstatus;
    private final int sykemeldingsgrad;
    private final int refusjonsbelop;

}
