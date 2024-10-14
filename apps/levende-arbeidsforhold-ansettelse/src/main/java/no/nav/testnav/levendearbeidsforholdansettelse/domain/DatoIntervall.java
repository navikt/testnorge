package no.nav.testnav.levendearbeidsforholdansettelse.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatoIntervall {

    private LocalDate tom;
    private LocalDate fom;
}
