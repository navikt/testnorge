package no.nav.testnav.libs.dto.personsearchservice.v1.search;


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
    List<String> forelderBarnRelasjoner;
}
