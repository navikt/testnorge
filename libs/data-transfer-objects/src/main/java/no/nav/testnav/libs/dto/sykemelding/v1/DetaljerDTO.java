package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetaljerDTO {

    private String tiltakArbeidsplass;
    private String tiltakNav;
    private String andreTiltak;
    private Boolean arbeidsforEtterEndtPeriode;
    private String beskrivHensynArbeidsplassen;
}

