package no.nav.registre.testnorge.personsearchservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.personsearchservice.controller.dto.PageDTO;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Search {
    @JsonProperty
    PageDTO page;
    @JsonProperty
    String tag;
}
