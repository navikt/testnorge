package no.nav.registre.sam.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class SamSaveInHodejegerenRequest {
    private String kilde;
    private List<IdentMedData> identMedData;
}
