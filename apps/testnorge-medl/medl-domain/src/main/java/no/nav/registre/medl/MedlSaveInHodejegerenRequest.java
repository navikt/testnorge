package no.nav.registre.medl;

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
public class MedlSaveInHodejegerenRequest {

    private String kilde;
    private List<IdentMedData> identMedData;
}
