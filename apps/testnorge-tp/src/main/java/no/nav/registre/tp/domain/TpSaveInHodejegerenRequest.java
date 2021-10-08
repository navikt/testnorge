package no.nav.registre.tp.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TpSaveInHodejegerenRequest {

    private String kilde;
    private List<IdentMedData> identMedData;
}
