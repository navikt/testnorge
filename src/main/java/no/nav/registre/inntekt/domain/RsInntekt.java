package no.nav.registre.inntekt.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class RsInntekt {

    private Double beloep;
    private String inntektstype;
    private String aar;
    private String maaned;
    private RsInntektsinformasjonsType inntektsinformasjonsType;
    private Boolean inngaarIGrunnlagForTrekk;
    private Boolean utloeserArbeidsgiveravgift;
    private String virksomhet;
    private String opplysningspliktig;
    private String beskrivelse;
    private String fordel;
}
