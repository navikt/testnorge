package no.nav.dolly.bestilling.pdlforvalter;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlFoedsel {

    private String fodekommune;
    private String foedeland;
    private String foedested;
    private Integer foedselsaar;
    private LocalDate foedselsdato;
    private String kilde;
}
