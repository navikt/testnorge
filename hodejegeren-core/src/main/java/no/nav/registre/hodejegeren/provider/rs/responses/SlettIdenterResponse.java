package no.nav.registre.hodejegeren.provider.rs.responses;

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
public class SlettIdenterResponse {

    private List<String> identerSomBleSlettetFraAvspillergruppe;
    private List<String> identerSomIkkeKunneBliSlettet;
    private List<String> identerMedGyldigStatusQuo;
}
