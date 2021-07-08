package no.nav.testnav.libs.dto.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetaljerDTO {

    private String tiltakArbeidsplass;
    private String tiltakNav;
    private Boolean arbeidsforEtterEndtPeriode;
    private String beskrivHensynArbeidsplassen;
}

