package no.nav.registre.arena.core.provider.rs.requests;

import lombok.*;
import no.nav.registre.arena.domain.NyBruker;

import java.util.List;

@AllArgsConstructor
@Getter
public class IdentMedData {
    private String id;
    private List<NyBruker> data;
}
