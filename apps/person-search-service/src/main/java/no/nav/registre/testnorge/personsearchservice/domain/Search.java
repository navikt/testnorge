package no.nav.registre.testnorge.personsearchservice.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Search {
    @JsonProperty
    Page page;
    @JsonProperty
    String tag;
}
