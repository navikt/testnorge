package no.nav.registre.arena.core.consumer.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class SyntetisterteBrukereResponse {
    int antallOpprettedeIdenter;
    List<String> opprettedeIdenter;
}
