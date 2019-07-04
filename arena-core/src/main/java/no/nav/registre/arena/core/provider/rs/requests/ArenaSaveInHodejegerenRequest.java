package no.nav.registre.arena.core.provider.rs.requests;

import lombok.*;
import no.nav.registre.arena.domain.NyBruker;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArenaSaveInHodejegerenRequest {
    private String kilde;
    private List<NyBruker> identerMedData;
}
