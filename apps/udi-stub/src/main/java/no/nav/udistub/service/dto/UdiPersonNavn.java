package no.nav.udistub.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UdiPersonNavn {
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
}
