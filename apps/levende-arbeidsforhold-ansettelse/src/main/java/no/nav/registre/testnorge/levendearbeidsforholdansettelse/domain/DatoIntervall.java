package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Builder
@Getter
@Setter
public class DatoIntervall {
    private LocalDate tom;
    private LocalDate from;
}
