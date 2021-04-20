package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlFoedsel extends PdlDbVersjon {

    private String fodekommune;
    private String foedeland;
    private String foedested;
    private Integer foedselsaar;
    private LocalDate foedselsdato;
    private String kilde;
}
