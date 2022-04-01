package no.nav.registre.testnorge.personsearchservice.controller.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Doedsfall {
    Boolean harDoedsdato;
    LocalDate fom;
    LocalDate tom;
}
