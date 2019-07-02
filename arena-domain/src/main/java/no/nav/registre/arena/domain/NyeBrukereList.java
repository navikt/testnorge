package no.nav.registre.arena.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NyeBrukereList {
    @JsonProperty
    private List<NyBruker> nyeBrukere;
}
