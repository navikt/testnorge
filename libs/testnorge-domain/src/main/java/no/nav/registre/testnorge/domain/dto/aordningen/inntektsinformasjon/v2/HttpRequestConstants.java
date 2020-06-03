package no.nav.registre.testnorge.domain.dto.aordningen.inntektsinformasjon.v2;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HttpRequestConstants {

    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String PARAM_NORSKE_IDENTER = "norske-identer";
    public static final String PARAM_HISTORIKK = "historikk";
    public static final String PARAM_VALIDER_KODEVERK = "valider-kodeverk";
    public static final String PARAM_VALIDER_INNTEKTSKOBINASJONER = "valider-inntektskombinasjoner";
    public static final String PATH_PERSONER = "/api/v2/personer";
    public static final String PATH_INNTEKTSINFORMASJON = "/api/v2/inntektsinformasjon";
    public static final String PATH_INNTEKT = "/api/v2/inntekt";
    public static final String PATH_FRADRAG = "/api/v2/fradrag";
    public static final String PATH_FORSKUDDSTREKK = "/api/v2/forskuddstrekk";
    public static final String PATH_ARBEIDSFORHOLD = "/api/v2/arbeidsforhold";
}
