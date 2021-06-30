package no.nav.registre.testnorge.arbeidsforholdservice.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.AntallTimerForTimeloennetDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsavtaleDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsforholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.PermisjonPermitteringDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.UtenlandsoppholdDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Aktoer;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.ArbeidsforholdResponse;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.ArbeidsforholdResponse.AntallTimerForTimeloennet;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.ArbeidsforholdResponse.Arbeidsavtale;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.ArbeidsforholdResponse.PermisjonPermittering;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.ArbeidsforholdResponse.Utenlandsopphold;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.Fartoy;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

@Component
public class ArbeidsforholdResponseMappingStrategy implements MappingStrategy {

    @Override
    public void register(MapperFactory factory) {
        factory.classMap(ArbeidsforholdDTO.class, ArbeidsforholdResponse.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsforholdDTO arbeidsforholdResponse, ArbeidsforholdResponse arbeidsforhold, MappingContext context) {

                        arbeidsforhold.setType(arbeidsforholdResponse.getType());
                        arbeidsforhold.setArbeidsforholdId(arbeidsforholdResponse.getArbeidsforholdId());

                        arbeidsforhold.setAntallTimerForTimeloennet(mapperFacade.mapAsList(arbeidsforholdResponse.getAntallTimerForTimeloennet(), AntallTimerForTimeloennet.class));

                        arbeidsforhold.setArbeidsgiver(arbeidsforholdResponse.getArbeidsgiver().getType().equals("Organisasjon")
                                ? ArbeidsforholdResponse.Arbeidsgiver.builder()
                                .type(Aktoer.Organisasjon)
                                .organisasjonsnummer(arbeidsforholdResponse.getArbeidsgiver().getOrganisasjonsnummer())
                                .build()
                                : ArbeidsforholdResponse.Arbeidsgiver.builder()
                                .type(Aktoer.Person)
                                .offentligIdent(arbeidsforholdResponse.getArbeidsgiver().getIdent())
                                .build()
                        );

                        arbeidsforhold.setAnsettelsesperiode(ArbeidsforholdResponse.Ansettelsesperiode.builder()
                                .periode(ArbeidsforholdResponse.Periode.builder()
                                        .fom(nonNull(arbeidsforholdResponse.getAnsettelsesperiode())
                                                && nonNull(arbeidsforholdResponse.getAnsettelsesperiode().getPeriode())
                                                ? arbeidsforholdResponse.getAnsettelsesperiode().getPeriode().getFom()
                                                : null)
                                        .tom(nonNull(arbeidsforholdResponse.getAnsettelsesperiode())
                                                && nonNull(arbeidsforholdResponse.getAnsettelsesperiode().getPeriode())
                                                ? arbeidsforholdResponse.getAnsettelsesperiode().getPeriode().getTom()
                                                : null)
                                        .build())
                                .sluttaarsak(arbeidsforholdResponse.getAnsettelsesperiode().getSluttaarsak())
                                .build());
                        if (nonNull(arbeidsforholdResponse.getArbeidsavtaler()) && !arbeidsforholdResponse.getArbeidsavtaler().isEmpty()) {
                            arbeidsforhold.setArbeidsavtaler(mapperFacade.mapAsList(arbeidsforholdResponse.getArbeidsavtaler(), Arbeidsavtale.class));

                            if (nonNull(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipstype())) {
                                arbeidsforhold.setFartoy(Fartoy.builder()
                                        .skipsregister(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipsregister())
                                        .skipstype(arbeidsforholdResponse.getArbeidsavtaler().get(0).getSkipstype())
                                        .fartsomraade(arbeidsforholdResponse.getArbeidsavtaler().get(0).getFartsomraade())
                                        .build());
                            }
                        }

                        arbeidsforhold.setArbeidstaker(ArbeidsforholdResponse.Arbeidstaker.builder()
                                .type(arbeidsforholdResponse.getArbeidstaker().getType().equals("Person")
                                        ? Aktoer.Person : Aktoer.Organisasjon)
                                .offentligIdent(arbeidsforholdResponse.getArbeidstaker().getOffentligIdent())
                                .build());

                        if (nonNull(arbeidsforholdResponse.getUtenlandsopphold())) {
                            arbeidsforhold.setUtenlandsopphold(mapperFacade.mapAsList(arbeidsforholdResponse.getUtenlandsopphold(), Utenlandsopphold.class));
                        }
                        if (nonNull(arbeidsforholdResponse.getPermisjonPermitteringer())) {
                            arbeidsforhold.setPermisjonPermitteringer(mapperFacade.mapAsList(arbeidsforholdResponse.getPermisjonPermitteringer(), PermisjonPermittering.class));
                        }
                    }
                })
                .register();

        factory.classMap(PermisjonPermitteringDTO.class, PermisjonPermittering.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(PermisjonPermitteringDTO permisjonPermitteringDTO, PermisjonPermittering permisjonDTO, MappingContext context) {

                        permisjonDTO.setPermisjonPermitteringId(permisjonPermitteringDTO.getPermisjonPermitteringId());
                        permisjonDTO.setType(permisjonPermitteringDTO.getType());
                        permisjonDTO.setProsent(permisjonPermitteringDTO.getProsent().doubleValue());
                        if (nonNull(permisjonPermitteringDTO.getPeriode())) {
                            permisjonDTO.setPeriode(ArbeidsforholdResponse.Periode.builder()
                                    .fom(permisjonPermitteringDTO.getPeriode().getFom())
                                    .tom(permisjonPermitteringDTO.getPeriode().getTom())
                                    .build());
                        }
                    }
                })
                .register();

        factory.classMap(UtenlandsoppholdDTO.class, Utenlandsopphold.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(UtenlandsoppholdDTO utenlandsoppholdDTO, Utenlandsopphold rsUtenlandsopphold, MappingContext context) {

                        rsUtenlandsopphold.setLandkode(utenlandsoppholdDTO.getLandkode());
                        if (nonNull(utenlandsoppholdDTO.getPeriode())) {
                            rsUtenlandsopphold.setPeriode(new ArbeidsforholdResponse.Periode(
                                    utenlandsoppholdDTO.getPeriode().getFom(),
                                    utenlandsoppholdDTO.getPeriode().getTom()));
                            rsUtenlandsopphold.setRapporteringsperiode(utenlandsoppholdDTO.getRapporteringsperiode());
                        }
                    }
                })
                .register();

        factory.classMap(AntallTimerForTimeloennetDTO.class, AntallTimerForTimeloennet.class)
                .byDefault()
                .register();


        factory.classMap(ArbeidsavtaleDTO.class, Arbeidsavtale.class)
                .customize(new CustomMapper<>() {
                    @Override
                    public void mapAtoB(ArbeidsavtaleDTO arbeidsavtaleDTO, Arbeidsavtale arbeidsavtale, MappingContext context) {

                        arbeidsavtale.setAntallTimerPrUke(nonNull(arbeidsavtaleDTO.getAntallTimerPrUke())
                                ? arbeidsavtaleDTO.getAntallTimerPrUke().doubleValue()
                                : null);
                        arbeidsavtale.setStillingsprosent(nonNull(arbeidsavtaleDTO.getStillingsprosent())
                                ? arbeidsavtaleDTO.getStillingsprosent().doubleValue()
                                : null);
                        arbeidsavtale.setSistStillingsendring(arbeidsavtaleDTO.getSistStillingsendring());

                    }
                })
                .exclude("antallTimerPrUke")
                .exclude("stillingsprosent")
                .exclude("beregnetAntallTimerPrUke")
                .byDefault()
                .register();
    }

}