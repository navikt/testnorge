package no.nav.dolly.domain.resultset.udistub.model;

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
public class UdiPersonNavn {

    private String etternavn;
    private String fornavn;
    private String mellomnavn;
}
