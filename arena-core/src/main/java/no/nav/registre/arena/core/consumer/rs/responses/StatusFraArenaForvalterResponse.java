package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StatusFraArenaForvalterResponse {
    private List<Arbeidsoker> arbeidsokerList;
    private int antallSider;
}