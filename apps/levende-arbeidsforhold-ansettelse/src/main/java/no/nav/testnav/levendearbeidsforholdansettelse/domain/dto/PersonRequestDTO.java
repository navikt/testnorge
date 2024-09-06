package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.DatoIntervall;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO {

    private DatoIntervall intervall;
    private String postnr;
}
