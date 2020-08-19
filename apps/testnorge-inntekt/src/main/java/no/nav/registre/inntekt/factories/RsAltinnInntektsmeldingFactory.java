package no.nav.registre.inntekt.factories;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.RsInntektsmeldingRequest;
import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsInntektsmelding;

public class RsAltinnInntektsmeldingFactory {

    private RsAltinnInntektsmeldingFactory() {}

    public static RsInntektsmelding create(RsInntektsmeldingRequest request, String ident) {
        return RsInntektsmelding.builder()
                .ytelse(request.getYtelse())
                .aarsakTilInnsending(request.getAarsakTilInnsending())
                .arbeidstakerFnr(ident)
                .naerRelasjon(request.isNaerRelasjon())
                .avsendersystem(request.getAvsendersystem())
                .arbeidsgiver(request.getArbeidsgiver())
                .arbeidsgiverPrivat(request.getArbeidsgiverPrivat())
                .arbeidsforhold(request.getArbeidsforhold())
                .refusjon(request.getRefusjon())
                .omsorgspenger(request.getOmsorgspenger())
                .sykepengerIArbeidsgiverperioden(request.getSykepengerIArbeidsgiverperioden())
                .startdatoForeldrepengeperiode(request.getStartdatoForeldrepengeperiode())
                .opphoerAvNaturalytelseListe(request.getOpphoerAvNaturalytelseListe())
                .gjenopptakelseNaturalytelseListe(request.getGjenopptakelseNaturalytelseListe())
                .pleiepengerPerioder(request.getPleiepengerPerioder())
                .build();
    }
}
