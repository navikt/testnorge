package no.nav.registre.arena.core.provider.rs.requests;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArenaSaveInHodejegerenRequest {
    private String kilde;
    private List<IdentMedData> identerMedData;
}
