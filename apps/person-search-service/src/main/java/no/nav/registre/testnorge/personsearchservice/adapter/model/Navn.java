package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Navn {
    String fornavn;
    String mellomnavn;
    String etternavn;
    LocalDate gyldigFraOgMed;
}
