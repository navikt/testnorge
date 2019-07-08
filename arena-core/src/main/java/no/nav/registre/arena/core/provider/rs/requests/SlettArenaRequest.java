package no.nav.registre.arena.core.provider.rs.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SlettArenaRequest {
    @JsonProperty
    String miljoe;
    @JsonProperty
    List<String> identer;
}
