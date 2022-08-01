package no.nav.registre.bisys.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barn {

    private String fnr;
    private LocalDate foedselsdato;
    private String morFnr;
    private String farFnr;
}
