package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LagreTpYtelseRequest {

    private Set<String> miljoer;

    private String fnr;

    private String ordning;
    private PensjonData.TpYtelseType ytelseType;
    private LocalDate datoInnmeldtYtelseFom;
    private LocalDate datoYtelseIverksattFom;
    private LocalDate datoYtelseIverksattTom;
}
