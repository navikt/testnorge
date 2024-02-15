package no.nav.dolly.bestilling.inntektsmelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.dokarkiv.v1.RsJoarkMetadata;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsDelvisFravaer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsEndringIRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsKontaktinformasjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsNaturalytelseDetaljer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsPeriode;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.Avsendertype;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;

@Component
public class InntektsmeldingMappingStrategy implements MappingStrategy {

    private static RsKontaktinformasjon getFiktivKontaktinformasjon() {

        return RsKontaktinformasjon.builder()
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

                        if (isNull(inntektsmelding.getJoarkMetadata())) {
                            inntektsmelding.setJoarkMetadata(new RsJoarkMetadata());
                        }

                        inntektsmelding.getJoarkMetadata().setAvsenderMottakerIdType(
                                (!rsInntektsmelding.getInntekter().isEmpty() &&
                                        nonNull(rsInntektsmelding.getInntekter().getFirst().getArbeidsgiver()) ?
                                        Avsendertype.ORGNR : Avsendertype.FNR).name());

                        inntektsmelding.setArbeidstakerFnr((String) context.getProperty("ident"));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.Inntektsmelding.class, RsInntektsmeldingRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.Inntektsmelding rsInntektsmelding,
                                        RsInntektsmeldingRequest inntektsmelding, MappingContext context) {

                        inntektsmelding.setAarsakTilInnsending(
                                nullcheckSetDefaultValue(inntektsmelding.getAarsakTilInnsending(), AarsakInnsendingKodeListe.NY));

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
                            inntektsmelding.setAvsendersystem(RsAvsendersystem.builder()
                                    .innsendingstidspunkt(LocalDateTime.now())
                                    .build());
                        }
                        inntektsmelding.getAvsendersystem().setSystemnavn("Dolly");
                        inntektsmelding.getAvsendersystem().setSystemversjon("2.0");

                        inntektsmelding.setStartdatoForeldrepengeperiode(toLocalDateTime(rsInntektsmelding.getStartdatoForeldrepengeperiode()));
                    }
                })

                .exclude("startdatoForeldrepengeperiode")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsArbeidsforhold.class, RsArbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsArbeidsforhold rsArbeidsforhold,
                                        RsArbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setFoersteFravaersdag(toLocalDateTime(rsArbeidsforhold.getFoersteFravaersdag()));
                    }
                })
                .exclude("foersteFravaersdag")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsDelvisFravaer.class, RsDelvisFravaer.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsDelvisFravaer rsDelvisFravaer,
                                        RsDelvisFravaer delvisFravaer, MappingContext context) {

                        delvisFravaer.setDato(toLocalDateTime(rsDelvisFravaer.getDato()));
                    }
                })
                .exclude("dato")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsNaturalYtelseDetaljer.class, RsNaturalytelseDetaljer.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsNaturalYtelseDetaljer rsNaturalYtelseDetaljer,
                                        RsNaturalytelseDetaljer naturalytelseDetaljer, MappingContext context) {

                        naturalytelseDetaljer.setFom(toLocalDateTime(rsNaturalYtelseDetaljer.getFom()));
                    }
                })
                .exclude("fom")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsRefusjon.class, RsRefusjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsRefusjon rsRefusjon,
                                        RsRefusjon refusjon, MappingContext context) {

                        refusjon.setRefusjonsopphoersdato(toLocalDateTime(rsRefusjon.getRefusjonsopphoersdato()));
                    }
                })
                .exclude("refusjonsopphoersdato")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsEndringIRefusjon.class, RsEndringIRefusjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsEndringIRefusjon rsRefusjon,
                                        RsEndringIRefusjon refusjon, MappingContext context) {

                        refusjon.setEndringsdato(toLocalDateTime(rsRefusjon.getEndringsdato()));
                    }
                })
                .exclude("endringsdato")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsPeriode.class, RsPeriode.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsPeriode rsPeriode,
                                        RsPeriode periode, MappingContext context) {

                        periode.setFom(toLocalDateTime(rsPeriode.getFom()));
                        periode.setTom(toLocalDateTime(rsPeriode.getTom()));
                    }
                })
                .register();
    }

    private static LocalDateTime toLocalDateTime(LocalDate localDate) {

        return nonNull(localDate) ? localDate.atStartOfDay() : null;
    }
}