package no.nav.registre.sigrun.consumer.rs.responses;

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
public class SigrunSkattegrunnlagResponse {

    private String personidentifikator;
    private String inntektsaar;
    private String tjeneste;
    private String grunnlag;
    private String verdi;
    private String testdataEier;
}
