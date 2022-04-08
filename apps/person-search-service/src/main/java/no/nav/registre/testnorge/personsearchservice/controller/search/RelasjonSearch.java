package no.nav.registre.testnorge.personsearchservice.controller.search;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonSearch {
    String harBarn;
    String harDoedfoedtBarn;
    List<String> forelderRelasjoner;
    String deltBosted;
}
