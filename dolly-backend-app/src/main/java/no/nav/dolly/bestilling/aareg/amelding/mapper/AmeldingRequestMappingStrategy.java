package no.nav.dolly.bestilling.aareg.amelding.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.aareg.amelding.domain.Virksomhet;
import no.nav.dolly.domain.resultset.aareg.RsAmeldingRequest;
import no.nav.dolly.domain.resultset.aareg.RsAntallTimerIPerioden;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforholdAareg;
import no.nav.dolly.domain.resultset.aareg.RsFartoy;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

@Component
public class AmeldingRequestMappingStrategy implements MappingStrategy {

    private static final String PERMISJON_ID = "dolly-123456";
    private static final Float DEFAULT_ARBEIDSTID = 37.5F;

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(RsAmeldingRequest.class, AMeldingDTO.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAmeldingRequest rsAmelding,
                                        AMeldingDTO amelding, MappingContext context) {

                        String[] date = rsAmelding.getMaaned().split("-");
                        amelding.setKalendermaaned(LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), 1));
                        Map<String, String> opplysningsPliktig = (Map<String, String>) context.getProperty("opplysningsPliktig");

                        amelding.setOpplysningspliktigOrganisajonsnummer(opplysningsPliktig.get(rsAmelding.getArbeidsforhold().get(0).getArbeidsgiver().getOrgnummer()));

                        List<Virksomhet> virksomheter = mapperFacade.mapAsList(rsAmelding.getArbeidsforhold(), Virksomhet.class, context);
                        List<VirksomhetDTO> ameldingVirksomheter = virksomheter.stream().map(virksomhet ->
                                        VirksomhetDTO.builder()
                                                .organisajonsnummer(virksomhet.getOrganisajonsnummer())
                                                .personer(virksomhet.getPersoner())
                                                .build())
                                .collect(Collectors.toList());

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
                                                nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode()) && nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode().getFom())
                                                        ? rsArbeidsforholdAareg.getAnsettelsesPeriode().getFom().toLocalDate()
                                                        : null)
                                        .sluttdato(
                                                nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode()) && nonNull(rsArbeidsforholdAareg.getAnsettelsesPeriode().getTom())
                                                        ? rsArbeidsforholdAareg.getAnsettelsesPeriode().getTom().toLocalDate()
                                                        : null)
                                        .antallTimerPerUke(
                                                !rsArbeidsforholdAareg.getAntallTimerForTimeloennet().isEmpty()
                                                        ? rsArbeidsforholdAareg.getAntallTimerForTimeloennet().get(0).getAntallTimer().floatValue()
                                                        : nonNull(rsArbeidsforholdAareg.getArbeidsavtale()) && nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getAvtaltArbeidstimerPerUke())
                                                        ? rsArbeidsforholdAareg.getArbeidsavtale().getAvtaltArbeidstimerPerUke().floatValue()
                                                        : DEFAULT_ARBEIDSTID)
                                        .arbeidsforholdId(nonNull(rsArbeidsforholdAareg.getArbeidsforholdID()) ? rsArbeidsforholdAareg.getArbeidsforholdID() : "1")
                                        .arbeidsforholdType((String) context.getProperty("arbeidsforholdstype"))
                                        .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                        .fartoey(nonNull(rsArbeidsforholdAareg.getFartoy()) && !rsArbeidsforholdAareg.getFartoy().isEmpty()
                                                ? mapperFacade.map(rsArbeidsforholdAareg.getFartoy().get(0), FartoeyDTO.class)
                                                : null)
                                        .inntekter(
                                                (nonNull(rsArbeidsforholdAareg.getUtenlandsopphold()) && !rsArbeidsforholdAareg.getUtenlandsopphold().isEmpty())
                                                        || (nonNull(rsArbeidsforholdAareg.getAntallTimerForTimeloennet()) && !rsArbeidsforholdAareg.getAntallTimerForTimeloennet().isEmpty())
                                                        ? Stream.of(
                                                                mapperFacade.mapAsList(rsArbeidsforholdAareg.getAntallTimerForTimeloennet(), InntektDTO.class),
                                                                mapperFacade.mapAsList(rsArbeidsforholdAareg.getUtenlandsopphold(), InntektDTO.class))
                                                        .flatMap(Collection::stream).collect(Collectors.toList())
                                                        : null)
                                        .yrke(rsArbeidsforholdAareg.getArbeidsavtale().getYrke())
                                        .arbeidstidsordning(rsArbeidsforholdAareg.getArbeidsavtale().getArbeidstidsordning())
                                        .stillingsprosent(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent()) ? rsArbeidsforholdAareg.getArbeidsavtale().getStillingsprosent().floatValue() : null)
                                        .sisteLoennsendringsdato(nonNull(rsArbeidsforholdAareg.getArbeidsavtale().getSisteLoennsendringsdato()) ? rsArbeidsforholdAareg.getArbeidsavtale().getSisteLoennsendringsdato().toLocalDate() : null)
                                        .permisjoner((nonNull(rsArbeidsforholdAareg.getPermisjon()) && !rsArbeidsforholdAareg.getPermisjon().isEmpty())
                                                || (nonNull(rsArbeidsforholdAareg.getPermittering()) && !rsArbeidsforholdAareg.getPermittering().isEmpty())
                                                ? Stream.of(
                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermisjon(), PermisjonDTO.class),
                                                        mapperFacade.mapAsList(rsArbeidsforholdAareg.getPermittering(), PermisjonDTO.class))
                                                .flatMap(Collection::stream).collect(Collectors.toList())
                                                : null)
                                        .avvik(null) // Settes dersom avvik kommer fra frontend
                                        .build()))
                                .build()
                        ));
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsFartoy.class, FartoeyDTO.class)
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
                                nonNull(rsPermisjon.getPermisjonsPeriode()) && nonNull(rsPermisjon.getPermisjonsPeriode().getFom())
                                        ? rsPermisjon.getPermisjonsPeriode().getFom().toLocalDate()
                                        : null);
                        permisjonDTO.setSluttdato(
                                nonNull(rsPermisjon.getPermisjonsPeriode()) && nonNull(rsPermisjon.getPermisjonsPeriode().getTom())
                                        ? rsPermisjon.getPermisjonsPeriode().getTom().toLocalDate()
                                        : null);
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
                                .fom(nonNull(rsPermittering.getPermitteringsPeriode()) && nonNull(rsPermittering.getPermitteringsPeriode().getFom())
                                        ? rsPermittering.getPermitteringsPeriode().getFom()
                                        : null)
                                .tom(nonNull(rsPermittering.getPermitteringsPeriode()) && nonNull(rsPermittering.getPermitteringsPeriode().getTom())
                                        ? rsPermittering.getPermitteringsPeriode().getTom()
                                        : null)
                                .build());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsUtenlandsopphold.class, InntektDTO.class).customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsUtenlandsopphold utenlandsopphold, InntektDTO inntekt, MappingContext context) {
                        inntekt.setStartdatoOpptjeningsperiode(nonNull(utenlandsopphold.getPeriode().getFom()) ? utenlandsopphold.getPeriode().getFom().toLocalDate() : null);
                        inntekt.setSluttdatoOpptjeningsperiode(nonNull(utenlandsopphold.getPeriode().getTom()) ? utenlandsopphold.getPeriode().getTom().toLocalDate() : null);
                        inntekt.setOpptjeningsland(utenlandsopphold.getLand());
                    }
                })
                .byDefault()
                .register();

        factory.classMap(RsAntallTimerIPerioden.class, InntektDTO.class).customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(RsAntallTimerIPerioden antallTimerIPerioden, InntektDTO inntekt, MappingContext context) {
                        inntekt.setStartdatoOpptjeningsperiode(nonNull(antallTimerIPerioden.getPeriode().getFom()) ? antallTimerIPerioden.getPeriode().getFom().toLocalDate() : null);
                        inntekt.setSluttdatoOpptjeningsperiode(nonNull(antallTimerIPerioden.getPeriode().getTom()) ? antallTimerIPerioden.getPeriode().getTom().toLocalDate() : null);
                        inntekt.setAntall(antallTimerIPerioden.getAntallTimer().intValue());
                    }
                })
                .byDefault()
                .register();
    }
}