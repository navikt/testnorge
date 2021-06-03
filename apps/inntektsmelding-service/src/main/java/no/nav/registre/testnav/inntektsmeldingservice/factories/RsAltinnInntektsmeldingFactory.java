package no.nav.registre.testnav.inntektsmeldingservice.factories;


import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.RsInntektsmeldingRequest;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs.RsInntektsmelding;

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
