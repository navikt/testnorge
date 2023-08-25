package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PensjonSamboerRequest {

    private String pidBruker;
    private String pidSamboer;
    private LocalDate datoFom;
    private LocalDate datoTom;
    private String registrertAv;
}
