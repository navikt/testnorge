package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
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
