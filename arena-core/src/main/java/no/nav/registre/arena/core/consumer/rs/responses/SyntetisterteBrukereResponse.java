package no.nav.registre.arena.core.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SyntetisterteBrukereResponse {
    @JsonProperty("antallOpprettedeIdenter")
    int antallOpprettedeIdenter;
    @JsonProperty("opprettedeIdenter")
    List<String> opprettedeIdenter;
}
