package no.nav.registre.testnorge.personsearchservice.controller.search;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RelasjonSearch {
    String harBarn;
    String harDoedfoedtBarn;
    Boolean barn;
    Boolean doedfoedtBarn;
    Boolean mor;
    Boolean far;
}
