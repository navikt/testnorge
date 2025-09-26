package no.nav.dolly.bestilling.inntektsmelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.dokarkiv.v1.RsJoarkMetadata;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.AarsakInnsendingKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums.YtelseKodeListe;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsforhold;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiver;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsArbeidsgiverPrivat;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsAvsendersystem;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsDelvisFravaer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsEndringIRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsKontaktinformasjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsNaturalytelseDetaljer;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsOmsorgspenger;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsPeriode;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsRefusjon;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsSykepengerIArbeidsgiverperioden;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingRequest.Avsendertype;
import static no.nav.dolly.util.DateZoneUtil.CET;
import static no.nav.dolly.util.NullcheckUtil.nullcheckSetDefaultValue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Component
public class InntektsmeldingMappingStrategy implements MappingStrategy {

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
                        inntektsmelding.setMiljoe((String) context.getProperty("miljoe"));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.Inntektsmelding.class, RsInntektsmeldingRequest.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.Inntektsmelding rsInntektsmelding,
                                        RsInntektsmeldingRequest inntektsmelding, MappingContext context) {

                        inntektsmelding.setAarsakTilInnsending(nonNull(rsInntektsmelding.getAarsakTilInnsending()) ?
                                mapperFacade.map(rsInntektsmelding.getAarsakTilInnsending(), AarsakInnsendingKodeListe.class) :
                                AarsakInnsendingKodeListe.NY);

                        if (nonNull(rsInntektsmelding.getArbeidsgiver())) {

                            inntektsmelding.setArbeidsgiver(RsArbeidsgiver.builder()
                                    .kontaktinformasjon(
                                            nullcheckSetDefaultValue(
                                                    mapperFacade.map(rsInntektsmelding.getArbeidsgiver().getKontaktinformasjon(),
                                                            RsKontaktinformasjon.class),
                                                    getFiktivKontaktinformasjon()))
                                    .virksomhetsnummer(rsInntektsmelding.getArbeidsgiver().getVirksomhetsnummer())
                                    .build());
                        }

                        if (nonNull(rsInntektsmelding.getArbeidsgiverPrivat())) {

                            inntektsmelding.setArbeidsgiverPrivat(RsArbeidsgiverPrivat.builder()
                                    .kontaktinformasjon(
                                            nullcheckSetDefaultValue(
                                                    mapperFacade.map(rsInntektsmelding.getArbeidsgiverPrivat().getKontaktinformasjon(),
                                                            RsKontaktinformasjon.class),
                                                    getFiktivKontaktinformasjon()))
                                    .arbeidsgiverFnr(rsInntektsmelding.getArbeidsgiverPrivat().getArbeidsgiverFnr())
                                    .build());
                        }

                        if (isNull(inntektsmelding.getAvsendersystem())) {
                            inntektsmelding.setAvsendersystem(RsAvsendersystem.builder()
                                    .innsendingstidspunkt(LocalDateTime.now(CET))
                                    .build());
                        }
                        inntektsmelding.getAvsendersystem().setSystemnavn("Dolly");
                        inntektsmelding.getAvsendersystem().setSystemversjon("2.0");

                        inntektsmelding.setStartdatoForeldrepengeperiode(nonNull(rsInntektsmelding.getStartdatoForeldrepengeperiode())?
                                rsInntektsmelding.getStartdatoForeldrepengeperiode().toString() : null);

                        inntektsmelding.setArbeidsforhold(mapperFacade.map(rsInntektsmelding.getArbeidsforhold(), RsArbeidsforhold.class));

                        inntektsmelding.setNaerRelasjon(isTrue(rsInntektsmelding.getNaerRelasjon()));
                        inntektsmelding.setOmsorgspenger(mapperFacade.map(rsInntektsmelding.getOmsorgspenger(), RsOmsorgspenger.class));

                        inntektsmelding.setOpphoerAvNaturalytelseListe(
                                rsInntektsmelding
                                        .getOpphoerAvNaturalytelseListe()
                                        .stream()
                                        .map(ytelse -> {
                                            var mapped = mapperFacade.map(ytelse, RsNaturalytelseDetaljer.class);
                                            Optional
                                                    .ofNullable(ytelse.getNaturalytelseType())
                                                    .ifPresent(type -> mapped.setNaturalytelseType(type.getJsonValue()));
                                            return mapped;
                                        })
                                        .toList()
                        );
                        inntektsmelding.setGjenopptakelseNaturalytelseListe(
                                rsInntektsmelding
                                        .getGjenopptakelseNaturalytelseListe()
                                        .stream()
                                        .map(ytelse -> {
                                            var mapped = mapperFacade.map(ytelse, RsNaturalytelseDetaljer.class);
                                            Optional
                                                    .ofNullable(ytelse.getNaturalytelseType())
                                                    .ifPresent(type -> mapped.setNaturalytelseType(type.getJsonValue()));
                                            return mapped;
                                        })
                                .toList()
                        );

                        inntektsmelding.setPleiepengerPerioder(rsInntektsmelding.getPleiepengerPerioder().stream()
                                .map(periode -> mapperFacade.map(periode, RsPeriode.class))
                                .toList());

                        inntektsmelding.setRefusjon(mapperFacade.map(rsInntektsmelding.getRefusjon(), RsRefusjon.class));
                        inntektsmelding.setSykepengerIArbeidsgiverperioden(
                                mapperFacade.map(rsInntektsmelding.getSykepengerIArbeidsgiverperioden(),
                                        RsSykepengerIArbeidsgiverperioden.class));
                        inntektsmelding.setYtelse(mapperFacade.map(rsInntektsmelding.getYtelse(), YtelseKodeListe.class));
                    }
                })
                .register();

        factory.classMap(RsInntektsmelding.RsSykepengerIArbeidsgiverperioden.class, RsSykepengerIArbeidsgiverperioden.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsSykepengerIArbeidsgiverperioden rsSykepengerIArbeidsgiverperioden,
                                        RsSykepengerIArbeidsgiverperioden sykepengerIArbeidsgiverperioden, MappingContext context) {
                        if (isNull(rsSykepengerIArbeidsgiverperioden.getArbeidsgiverperiodeListe()) || rsSykepengerIArbeidsgiverperioden.getArbeidsgiverperiodeListe().isEmpty()) {
                            sykepengerIArbeidsgiverperioden.setArbeidsgiverperiodeListe(null);
                        } else
                            sykepengerIArbeidsgiverperioden.setArbeidsgiverperiodeListe(mapperFacade.mapAsList(rsSykepengerIArbeidsgiverperioden.getArbeidsgiverperiodeListe(), RsPeriode.class));
                    }
                })
                .exclude("arbeidsgiverperiodeListe")
                .byDefault()
                .register();

        factory.classMap(RsInntektsmelding.RsArbeidsforhold.class, RsArbeidsforhold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsArbeidsforhold rsArbeidsforhold,
                                        RsArbeidsforhold arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setFoersteFravaersdag(nonNull(rsArbeidsforhold.getFoersteFravaersdag())?
                                rsArbeidsforhold.getFoersteFravaersdag().toString() : null);
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

                        delvisFravaer.setDato(nonNull(rsDelvisFravaer.getDato()) ? rsDelvisFravaer.getDato().toString() : null);
                        delvisFravaer.setTimer(rsDelvisFravaer.getTimer());
                    }
                })
                .register();

        factory.classMap(RsInntektsmelding.RsNaturalYtelseDetaljer.class, RsNaturalytelseDetaljer.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsNaturalYtelseDetaljer rsNaturalYtelseDetaljer,
                                        RsNaturalytelseDetaljer naturalytelseDetaljer, MappingContext context) {

                        naturalytelseDetaljer.setFom(nonNull(rsNaturalYtelseDetaljer.getFom()) ?
                                rsNaturalYtelseDetaljer.getFom().toString() : null);
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

                        refusjon.setRefusjonsopphoersdato(nonNull(rsRefusjon.getRefusjonsopphoersdato()) ?
                                rsRefusjon.getRefusjonsopphoersdato().toString() : null);
                        refusjon.setEndringIRefusjonListe(rsRefusjon.getEndringIRefusjonListe().stream()
                                .map(refusjon1 -> mapperFacade.map(refusjon1, RsEndringIRefusjon.class))
                                .toList());
                        refusjon.setRefusjonsbeloepPrMnd(rsRefusjon.getRefusjonsbeloepPrMnd());
                    }
                })
                .register();

        factory.classMap(RsInntektsmelding.RsEndringIRefusjon.class, RsEndringIRefusjon.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsEndringIRefusjon rsRefusjon,
                                        RsEndringIRefusjon refusjon, MappingContext context) {

                        refusjon.setEndringsdato(nonNull(rsRefusjon.getEndringsdato()) ?
                                rsRefusjon.getEndringsdato().toString() : null);
                        refusjon.setRefusjonsbeloepPrMnd(rsRefusjon.getRefusjonsbeloepPrMnd());
                    }
                })
                .register();

        factory.classMap(RsInntektsmelding.RsPeriode.class, RsPeriode.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsInntektsmelding.RsPeriode rsPeriode,
                                        RsPeriode periode, MappingContext context) {

                        periode.setFom(nonNull(rsPeriode.getFom()) ? rsPeriode.getFom().toString() : null);
                        periode.setTom(nonNull(rsPeriode.getTom()) ? rsPeriode.getTom().toString() : null);
                    }
                })
                .register();
    }

    private static RsKontaktinformasjon getFiktivKontaktinformasjon() {

        return RsKontaktinformasjon.builder()
                .kontaktinformasjonNavn("Dolly Dollesen")
                .telefonnummer("99999999")
                .build();
    }
}
