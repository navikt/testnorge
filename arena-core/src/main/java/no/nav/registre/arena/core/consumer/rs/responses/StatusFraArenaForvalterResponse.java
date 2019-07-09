package no.nav.registre.arena.core.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StatusFraArenaForvalterResponse {
    @JsonProperty
    private List<Arbeidsoker> arbeidsokerList;
    @JsonProperty
    private int antallSider; // 0
}