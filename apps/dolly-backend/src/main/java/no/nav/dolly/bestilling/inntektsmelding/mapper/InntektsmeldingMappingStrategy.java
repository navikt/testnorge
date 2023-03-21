package no.nav.dolly.bestilling.inntektsmelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.Avsendersystem;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.Avsendertype;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.Inntektsmelding;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.JoarkMetadata;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.Kontaktinformasjon;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
public class InntektsmeldingMappingStrategy implements MappingStrategy {

    private static Kontaktinformasjon getFiktivKontaktinformasjon() {

        return Kontaktinformasjon.builder()
                .kontaktinformasjonNavn("Dolly Dollesen")
                .telefonnummer("99999999")
                .build();
    }

    @Override
    public void register(MapperFactory factory) {

        factory.classMap(RsInntektsmelding.class, InntektsmeldingRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding rsInntektsmelding,
                                        InntektsmeldingRequest inntektsmelding, MappingContext context) {

                        if (isNull(rsInntektsmelding.getJoarkMetadata())) {
                            inntektsmelding.setJoarkMetadata(new JoarkMetadata());
                        }
                        inntektsmelding.getJoarkMetadata().setAvsenderMottakerIdType(
                                !inntektsmelding.getInntekter().isEmpty() &&
                                        nonNull(inntektsmelding.getInntekter().get(0).getArbeidsgiver()) ?
                                        Avsendertype.ORGNR : Avsendertype.FNR
                        );
                        inntektsmelding.setArbeidstakerFnr((String) context.getProperty("ident"));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.Inntektsmelding.class, Inntektsmelding.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.Inntektsmelding rsInntektsmelding,
                                        Inntektsmelding inntektsmelding, MappingContext context) {

                        inntektsmelding.setAarsakTilInnsending(
                                nullcheckSetDefaultValue(rsInntektsmelding.getAarsakTilInnsending(), AarsakInnsendingKoder.NY));

                        if (nonNull(inntektsmelding.getArbeidsgiver()) &&
                                isNull(inntektsmelding.getArbeidsgiver().getKontaktinformasjon())) {
                            inntektsmelding.getArbeidsgiver().setKontaktinformasjon(
                                    getFiktivKontaktinformasjon());
                        }
                        if (nonNull(inntektsmelding.getArbeidsgiverPrivat()) &&
                                isNull(inntektsmelding.getArbeidsgiverPrivat().getKontaktinformasjon())) {
                            inntektsmelding.getArbeidsgiverPrivat().setKontaktinformasjon(
                                    getFiktivKontaktinformasjon());
                        }
                        if (isNull(inntektsmelding.getAvsendersystem())) {
                            inntektsmelding.setAvsendersystem(Avsendersystem.builder()
                                    .innsendingstidspunkt(LocalDateTime.now())
                                    .build());
                        }
                        inntektsmelding.getAvsendersystem().setSystemnavn("Dolly");
                        inntektsmelding.getAvsendersystem().setSystemversjon("2.0");
                    }
                })
                .byDefault()
                .register();
    }
}
