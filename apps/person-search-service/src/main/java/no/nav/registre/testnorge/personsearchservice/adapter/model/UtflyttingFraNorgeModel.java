package no.nav.registre.testnorge.personsearchservice.adapter.model;

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
