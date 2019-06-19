package no.nav.registre.inst.provider.rs.responses;

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
public class SletteOppholdResponse {

    private Map<String, List<String>> identerMedOppholdIdSomIkkeKunneSlettes;
    private Map<String, List<String>> identerMedOppholdIdSomBleSlettet;
}
