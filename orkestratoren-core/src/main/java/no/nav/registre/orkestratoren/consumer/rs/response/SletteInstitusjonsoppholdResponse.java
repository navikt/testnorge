package no.nav.registre.orkestratoren.consumer.rs.response;

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
public class SletteInstitusjonsoppholdResponse {

    private Map<String, List<String>> identerMedOppholdIdSomIkkeKunneSlettes;
    private Map<String, List<String>> identerMedOppholdIdSomBleSlettet;
}
