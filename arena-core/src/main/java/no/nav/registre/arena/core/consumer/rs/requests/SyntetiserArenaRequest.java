package no.nav.registre.arena.core.consumer.rs.requests;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SyntetiserArenaRequest {

    @JsonProperty("avspillergruppeId")
    private Long avspillergruppeId;
    @JsonProperty("miljoe")
    private String miljoe;
    @JsonProperty("antallNyeIdenter")
    private int antallNyeIdenter;
}