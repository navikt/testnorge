package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class UtflyttingFraNorgeModel implements WithMetadata {
    Metadata metadata;
    String tilflyttingsland;
    String tilflyttingsstedIUtlandet;
    LocalDate utflyttingsdato;
}
