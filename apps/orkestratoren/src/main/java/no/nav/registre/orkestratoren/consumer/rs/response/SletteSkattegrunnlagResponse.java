package no.nav.registre.orkestratoren.consumer.rs.response;

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
public class SletteSkattegrunnlagResponse {

    private List<SigrunSkattegrunnlagResponse> grunnlagSomIkkeKunneSlettes;

    private List<SigrunSkattegrunnlagResponse> grunnlagSomBleSlettet;

    private List<String> identerMedGrunnlagFraAnnenTestdataEier;
}
