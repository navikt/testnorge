package no.nav.registre.sigrun.domain;

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
public class SigrunSaveInHodejegerenRequest {

    private String kilde;
    private List<IdentMedData> identMedData;
}
