package no.nav.registre.sigrun.provider.rs.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import no.nav.registre.sigrun.consumer.rs.responses.SigrunSkattegrunnlagResponse;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SletteGrunnlagResponse {

    private List<SigrunSkattegrunnlagResponse> grunnlagSomIkkeKunneSlettes;

    private List<SigrunSkattegrunnlagResponse> grunnlagSomBleSlettet;

    private List<String> identerMedGrunnlagFraAnnenTestdataEier;
}
