package no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenererAltinnInntektRequest {

    private Long avspillergruppeId;

    private String miljoe;

    private Integer antallIdenter;

    private Integer prosentSomStemmerMedInntektskomponenten;
}
