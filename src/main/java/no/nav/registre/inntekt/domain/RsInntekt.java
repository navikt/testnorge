package no.nav.registre.inntekt.domain;

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
public class RsInntekt {

    private String fnr;
    private Integer beloep;
    private String inntektstype;
    private String aar;
    private String maaned;
    private RsInntektsinformasjonsType inntektsinformasjonsType;
    private Boolean inngaarIGrunnlagForTrekk;
    private Boolean utloeserArbeidsgiveravgift;
    private String virksomhet;
}
