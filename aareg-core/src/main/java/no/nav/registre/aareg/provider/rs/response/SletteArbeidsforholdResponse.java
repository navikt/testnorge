package no.nav.registre.aareg.provider.rs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SletteArbeidsforholdResponse {

    private Map<String, List<Long>> identermedArbeidsforholdIdSomBleSlettet;
    private List<String> identerSomIkkeKunneSlettes;
}
