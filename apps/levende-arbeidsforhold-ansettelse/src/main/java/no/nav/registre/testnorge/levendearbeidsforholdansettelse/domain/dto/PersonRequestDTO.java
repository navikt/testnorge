package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDTO {

    private int resultsPerPage = 100;
    private String from;
    private String to;
    private String postnr;
}
