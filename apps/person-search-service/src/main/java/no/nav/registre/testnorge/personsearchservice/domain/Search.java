package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.personsearchservice.controller.dto.PageDTO;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Search {
    PageDTO page;
    String tag;
}
