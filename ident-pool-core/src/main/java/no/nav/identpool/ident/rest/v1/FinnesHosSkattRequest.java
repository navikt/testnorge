package no.nav.identpool.ident.rest.v1;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FinnesHosSkattRequest {
    private String personidentifikator;
    private LocalDate foedselsdato;
}
