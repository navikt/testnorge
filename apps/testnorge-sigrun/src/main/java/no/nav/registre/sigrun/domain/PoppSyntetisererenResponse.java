package no.nav.registre.sigrun.domain;

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
public class PoppSyntetisererenResponse {

    private List<SyntetiskGrunnlag> grunnlag;
    private String inntektsaar;
    private String personidentifikator;
    private String testdataEier;
    private String tjeneste;
}
