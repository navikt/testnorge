package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlSikkerhetstiltak extends PdlOpplysning {

    private String tiltakstype;
    private String beskrivelse;
    private LocalDate gyldigFraOgMed;
    private LocalDate gyldigTilOgMed;
}
