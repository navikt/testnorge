package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import lombok.Getter;

@Getter
public enum Inntektstype {
    LOENNSINNTEKT("loennsinntekt"),
    YTELSE_FRA_OFFENTLIGE("ytelseFraOffentlige"),
    PENSJON_ELLER_TRYGD("pensjonEllerTrygd"),
    NAERINGSINNTEKT("naeringsinntekt");

    String navn;

    private Inntektstype(String navn) {
        this.navn = navn;
    }
}
