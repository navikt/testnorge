package no.nav.registre.arena.core.provider.rs.requests;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;

import no.nav.registre.arena.domain.NyBruker;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
public class NyeBrukereRequest {

    @JsonProperty("nyeBrukere")
    private List<NyBruker> nyeBrukere;

}
