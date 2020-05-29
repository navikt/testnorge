package no.nav.registre.aareg;

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
public class AaregSaveInHodejegerenRequest {

    private String kilde;
    private List<IdentMedData> identMedData;
}
