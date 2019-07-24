package no.nav.registre.arena.core.provider.rs.requests;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
public class ArenaSaveInHodejegerenRequest {
    private String kilde;
    private List<IdentMedData> identMedData;
}