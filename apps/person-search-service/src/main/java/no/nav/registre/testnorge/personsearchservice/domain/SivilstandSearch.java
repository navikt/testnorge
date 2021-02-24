package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SivilstandSearch {
    String type;
}
