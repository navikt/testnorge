package no.nav.registre.elsam.domain;

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
public class Detaljer {

    private String tiltakArbeidsplass;
    private String tiltakNav;
    private Boolean arbeidsforEtterEndtPeriode;
    private String beskrivHensynArbeidsplassen;
}
