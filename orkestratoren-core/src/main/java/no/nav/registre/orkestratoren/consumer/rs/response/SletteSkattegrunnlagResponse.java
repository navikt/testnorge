package no.nav.registre.orkestratoren.consumer.rs.response;

import java.util.List;

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
public class SletteSkattegrunnlagResponse {

    private List<SigrunSkattegrunnlagResponse> grunnlagSomIkkeKunneSlettes;

    private List<SigrunSkattegrunnlagResponse> grunnlagSomBleSlettet;

    private List<String> identerMedGrunnlagFraAnnenTestdataEier;
}
