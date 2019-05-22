package no.nav.registre.aareg;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AaregSaveInHodejegerenRequest {
    private String kilde;
    private List<IdentMedData> data;
}
