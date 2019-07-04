package no.nav.registre.arena.core.provider.rs.requests;

import lombok.*;
import no.nav.registre.arena.domain.NyBruker;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class IdentMedData {
    private String id;
    private List<NyBruker> data;
}
