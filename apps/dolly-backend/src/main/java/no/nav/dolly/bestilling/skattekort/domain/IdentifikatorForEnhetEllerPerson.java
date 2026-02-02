package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentifikatorForEnhetEllerPerson {

    private String organisasjonsnummer;
    private String personidentifikator;

}
