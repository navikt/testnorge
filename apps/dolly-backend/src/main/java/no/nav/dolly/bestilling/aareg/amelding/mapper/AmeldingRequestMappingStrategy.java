package no.nav.dolly.bestilling.aareg.amelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.amelding.domain.Virksomhet;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.aareg.RsPeriodeAareg;
import no.nav.dolly.domain.resultset.aareg.RsPermisjon;
import no.nav.dolly.domain.resultset.aareg.RsPermittering;
import no.nav.dolly.domain.resultset.aareg.RsUtenlandsopphold;
import no.nav.dolly.mapper.MappingStrategy;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.FartoeyDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.InntektDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.PermisjonDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.ameldingservice.v1.VirksomhetDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class AmeldingRequestMappingStrategy implements MappingStrategy {

    private static final String PERMISJON_ID = "dolly-123456";
    private static final Float DEFAULT_ARBEIDSTID = 37.5F;

    private static LocalDate getDate(LocalDateTime dateTime) {

        return nonNull(dateTime) ? dateTime.toLocalDate() : null;
    }

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAmeldingRequest.class, AMeldingDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAmeldingRequest rsAmelding,
                                        AMeldingDTO amelding, MappingContext context) {

                        var date = rsAmelding.getMaaned().split("-");
                        amelding.setKalendermaaned(LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), 1));
                        var opplysningsPliktig = (Map<String, String>) context.getProperty("opplysningspliktig");

                        amelding.setOpplysningspliktigOrganisajonsnummer(opplysningsPliktig.get(rsAmelding.getArbeidsforhold().get(0).getArbeidsgiver().getOrgnummer()));

                        var virksomheter = mapperFacade.mapAsList(rsAmelding.getArbeidsforhold(), Virksomhet.class, context);
                        virksomheter.forEach(virksomhet -> {
                            var arbForholdId = new AtomicInteger(0);
                            virksomhet.getPersoner()
                                    .forEach(person -> person.getArbeidsforhold()
                                            .forEach(arbeidsforhold -> arbeidsforhold
                                                    .setArbeidsforholdId(Integer.toString(arbForholdId.incrementAndGet()))));
                        });
                        var ameldingVirksomheter = virksomheter.stream().map(virksomhet ->
                                        VirksomhetDTO.builder()
                                                .organisajonsnummer(virksomhet.getOrganisajonsnummer())
                                                .personer(virksomhet.getPersoner())
                                                .build())
                                .toList();

                        amelding.setVirksomheter(ameldingVirksomheter);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsArbeidsforholdAareg.class, Virksomhet.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsArbeidsforholdAareg rsArbeidsforholdAareg, Virksomhet virksomhet, MappingContext context) {

                        virksomhet.setOrganisajonsnummer(rsArbeidsforholdAareg.getArbeidsgiver().getOrgnummer());
                        virksomhet.setPersoner(List.of(PersonDTO.builder()
                                .ident((String) context.getProperty("personIdent"))
                                .arbeidsforhold(List.of(ArbeidsforholdDTO.builder()
                                        .startdato(
                                                nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode()) ?
                                                        getDate(rsArbeidsforholdAareg.getAnsettelsesPeriode().getFom()) : null)
                                        .sluttdato(
                                                nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode()) ?
                                                        getDate(rsArbeidsforholdAareg.getAnsettelsesPeriode().getTom()) : null)
                                        .antallTimerPerUke(
                                                !rsArbeidsforholdAareg.getAntallTimerForTimeloennet().isEmpty() ?
                                                        rsArbeidsforholdAareg.getAntallTimerForTimeloennet().get(0).getAntallTimer().floatValue() :
                                                        getAvtaltArbeidstidPerUke(rsArbeidsforholdAareg))
                                        .arbeidsforholdType((String) context.getProperty("arbeidsforholdstype"))
                                        .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                        .fartoey(nonNull(rsArbeidsforholdAareg.getFartoy()) && !rsArbeidsforholdAareg.getFartoy().isEmpty() ?
                                                mapperFacade.map(rsArbeidsforholdAareg.getFartoy().get(0), FartoeyDTO.class) : null)
                                        .inntekter(
                                                (nonNull(rsArbeidsforholdAareg.getUtenlandsopphold()) &&
                                                        !rsArbeidsforholdAareg.getUtenlandsopphold().isEmpty()) ||
                                                        (nonNull(rsArbeidsforholdAareg.getAntallTimerForTimeloennet()) &&
                                                                !rsArbeidsforholdAareg.getAntallTimerForTimeloennet().isEmpty()) ?
                                                        Stream.of(
                                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getAntallTimerForTimeloennet(), InntektDTO.class),
                                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getUtenlandsopphold(), InntektDTO.class))
                                                                .flatMap(Collection::stream)
                                                                .toList() : null)
                                        .yrke(rsArbeidsforholdAareg.getArbeidsavtale().getYrke())
                                        .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                        .stillingsprosent(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent()) ? rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent().floatValue() : null)
                                        .sisteLoennsendringsdato(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getSisteLoennsendringsdato()) ? rsArbeidsforholdAareg.getArbeidsavtale().getSisteLoennsendringsdato().toLocalDate() : null)
                                        .permisjoner((nonNull(rsArbeidsforholdAareg.getPermisjon()) && !rsArbeidsforholdAareg.getPermisjon().isEmpty()) ||
                                                (nonNull(rsArbeidsforholdAareg.getPermittering()) && !rsArbeidsforholdAareg.getPermittering().isEmpty()) ? Stream.of(
                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermisjon(), PermisjonDTO.class),
                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermittering(), PermisjonDTO.class))
                                                .flatMap(Collection::stream)
                                                .toList() : null)
                                        .avvik(null) // Settes dersom avvik kommer fra frontend
                                        .build()))
                                .build()
                        ));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPermisjon.class, PermisjonDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPermisjon rsPermisjon, PermisjonDTO permisjonDTO, MappingContext context) {

                        permisjonDTO.setPermisjonId(PERMISJON_ID);
                        permisjonDTO.setBeskrivelse(rsPermisjon.getPermisjon());
                        permisjonDTO.setPermisjonsprosent(rsPermisjon.getPermisjonsprosent().floatValue());
                        permisjonDTO.setStartdato(
                                nonNull(rsPermisjon.getPermisjonsPeriode()) ?
                                        getDate(rsPermisjon.getPermisjonsPeriode().getFom()) : null);
                        permisjonDTO.setSluttdato(
                                nonNull(rsPermisjon.getPermisjonsPeriode()) ?
                                        getDate(rsPermisjon.getPermisjonsPeriode().getTom()) : null);
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsPermittering.class, RsPermisjon.class).customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsPermittering rsPermittering, RsPermisjon rsPermisjon, MappingContext context) {

                        rsPermisjon.setPermisjonId(PERMISJON_ID);
                        rsPermisjon.setPermisjon("permittering");
                        rsPermisjon.setPermisjonsprosent(rsPermittering.getPermitteringsprosent());
                        rsPermisjon.setPermisjonsPeriode(RsPeriodeAareg.builder()
                                .fom(nonNull(rsPermittering.getPermitteringsPeriode()) ?
                                        rsPermittering.getPermitteringsPeriode().getFom() : null)
                                .tom(nonNull(rsPermittering.getPermitteringsPeriode()) ?
                                        rsPermittering.getPermitteringsPeriode().getTom() : null)
                                .build());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsUtenlandsopphold.class, InntektDTO.class).customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsUtenlandsopphold utenlandsopphold, InntektDTO inntekt, MappingContext context) {

                        inntekt.setStartdatoOpptjeningsperiode(getDate(utenlandsopphold.getPeriode().getFom()));
                        inntekt.setSluttdatoOpptjeningsperiode(getDate(utenlandsopphold.getPeriode().getTom()));
                        inntekt.setOpptjeningsland(utenlandsopphold.getLand());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsAntallTimerIPerioden.class, InntektDTO.class).customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAntallTimerIPerioden antallTimerIPerioden, InntektDTO inntekt, MappingContext context) {

                        inntekt.setStartdatoOpptjeningsperiode(getDate(antallTimerIPerioden.getPeriode().getFom()));
                        inntekt.setSluttdatoOpptjeningsperiode(getDate(antallTimerIPerioden.getPeriode().getTom()));
                        inntekt.setAntall(antallTimerIPerioden.getAntallTimer().intValue());
                    }
                })
                .byDefault()
                .register();
    }

    private float getAvtaltArbeidstidPerUke(RsArbeidsforholdAareg rsArbeidsforholdAareg) {

        return nonNull(rsArbeidsforholdAareg.getArbeidsavtale()) &&
                nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getAvtaltArbeidstimerPerUke()) ?
                rsArbeidsforholdAareg.getArbeidsavtale().getAvtaltArbeidstimerPerUke().floatValue() :
                DEFAULT_ARBEIDSTID;
    }
}