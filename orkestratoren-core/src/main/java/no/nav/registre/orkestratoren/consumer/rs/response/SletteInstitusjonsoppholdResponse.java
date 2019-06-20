package no.nav.registre.orkestratoren.consumer.rs.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SletteInstitusjonsoppholdResponse {

    private Map<String, List<String>> identerMedOppholdIdSomIkkeKunneSlettes;
    private Map<String, List<String>> identerMedOppholdIdSomBleSlettet;
}
