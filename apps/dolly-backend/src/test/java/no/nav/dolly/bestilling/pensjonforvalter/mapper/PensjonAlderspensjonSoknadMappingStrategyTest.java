package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonSoknadRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullPersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.SivilstandDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class PensjonAlderspensjonSoknadMappingStrategyTest {

    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(),
                new PensjonAlderspensjonSoknadMappingStrategy());
    }

    @Test
    void mapAlderspensjonUtenPartner_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("pdlRelasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                .ident("123")
                .person(PdlPerson.Person.builder()
                        .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                .type(PdlPerson.SivilstandType.SKILT_PARTNER)
                                .build()))
                        .build())
                .build()));
        context.setProperty("dollyRelasjoner", emptyList());

        var target = mapperFacade.map(pensjon, AlderspensjonSoknadRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
    }

    @Test
    void mapAlderspensjonMedPartnerOgBarn_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(1234)
                        .build()))
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("pdlRelasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("456")
                                        .build()))
                                .forelderBarnRelasjon(List.of(PdlPerson.ForelderBarnRelasjon.builder()
                                        .relatertPersonsIdent("789")
                                        .minRolleForPerson(PdlPerson.Rolle.FAR)
                                        .relatertPersonsRolle(PdlPerson.Rolle.BARN)
                                        .build()))
                                .build())
                        .build(),
                PdlPersonBolk.PersonBolk.builder()
                        .ident("456")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("123")
                                        .build()))
                                .forelderBarnRelasjon(List.of(PdlPerson.ForelderBarnRelasjon.builder()
                                        .relatertPersonsIdent("789")
                                        .minRolleForPerson(PdlPerson.Rolle.MOR)
                                        .relatertPersonsRolle(PdlPerson.Rolle.BARN)
                                        .build()))
                                .build())
                        .build()));
        context.setProperty("dollyRelasjoner", emptyList());

        var target = mapperFacade.map(pensjon, AlderspensjonSoknadRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));

        assertThat(target.getSivilstand(), is(equalTo(AlderspensjonSoknadRequest.SivilstandType.GIFT)));
        assertThat(target.getSivilstandDatoFom(), is(equalTo(LocalDate.of(1988, 1, 2))));

        assertThat(target.getRelasjonListe().getFirst().getRelasjonType(), is(equalTo(AlderspensjonSoknadRequest.RelasjonType.EKTEF)));
        assertThat(target.getRelasjonListe().getFirst().getSumAvForventetArbeidKapitalPensjonInntekt(), is(equalTo(1234)));
        assertThat(target.getRelasjonListe().getFirst().getHarFellesBarn(), is(true));
    }

    @Test
    void mapAlderspensjonMedSamboerSomDoed_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(1234)
                        .build()))
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("pdlRelasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                        .type(PdlPerson.SivilstandType.UGIFT)
                                        .build()))
                                .build())
                        .build(),
                PdlPersonBolk.PersonBolk.builder()
                        .ident("456")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1990, 1, 2))
                                        .type(PdlPerson.SivilstandType.UGIFT)
                                        .relatertVedSivilstand("123")
                                        .build()))
                                .doedsfall(List.of(new PdlPerson.Doedsfall(LocalDate.of(2020, 1, 1))))
                                .build())
                        .build()));
        context.setProperty("dollyRelasjoner", List.of(FullPersonDTO.builder()
                .person(PersonDTO.builder()
                        .ident("123")
                        .sivilstand(List.of(SivilstandDTO.builder()
                                .type(SivilstandDTO.Sivilstand.SAMBOER)
                                .relatertVedSivilstand("456")
                                .sivilstandsdato(LocalDateTime.of(2000, 1, 2, 0, 0))
                                .build()))
                        .build())
                .build()
        ));

        var target = mapperFacade.map(pensjon, AlderspensjonSoknadRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));

        assertThat(target.getSivilstand(), is(equalTo(AlderspensjonSoknadRequest.SivilstandType.UGIF)));
        assertThat(target.getSivilstandDatoFom(), is(equalTo(LocalDate.of(1988, 1, 2))));

        assertThat(target.getRelasjonListe().getFirst().getRelasjonType(), is(equalTo(AlderspensjonSoknadRequest.RelasjonType.SAMBO)));
        assertThat(target.getRelasjonListe().getFirst().getRelasjonFraDato(), is(equalTo(LocalDate.of(2000, 1, 2))));
        assertThat(target.getRelasjonListe().getFirst().getHarFellesBarn(), is(false));
        assertThat(target.getRelasjonListe().getFirst().getDodsdato(), is(equalTo(LocalDate.of(2020, 1, 1))));
    }

    @Test
    void mapAlderspensjonHarVaertGiftEnkemann_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(1234)
                        .build()))
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("pdlRelasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(
                                        PdlPerson.Sivilstand.builder()
                                                .gyldigFraOgMed(LocalDate.of(2010, 1, 2))
                                                .type(PdlPerson.SivilstandType.ENKE_ELLER_ENKEMANN)
                                                .build(),
                                        PdlPerson.Sivilstand.builder()
                                                .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                                .type(PdlPerson.SivilstandType.GIFT)
                                                .relatertVedSivilstand("456")
                                                .build()))
                                .build())
                        .build(),
                PdlPersonBolk.PersonBolk.builder()
                        .ident("456")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1990, 1, 2))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("123")
                                        .build()))
                                .doedsfall(List.of(new PdlPerson.Doedsfall(LocalDate.of(2010, 1, 2))))
                                .build())
                        .build()));
        context.setProperty("dollyRelasjoner", emptyList());

        var target = mapperFacade.map(pensjon, AlderspensjonSoknadRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));

        assertThat(target.getSivilstand(), is(equalTo(AlderspensjonSoknadRequest.SivilstandType.ENKE)));
        assertThat(target.getSivilstandDatoFom(), is(equalTo(LocalDate.of(2010, 1, 2))));

        assertThat(target.getRelasjonListe().getFirst().getFnr(), is(equalTo("456")));
        assertThat(target.getRelasjonListe().getFirst().getRelasjonType(), is(equalTo(AlderspensjonSoknadRequest.RelasjonType.EKTEF)));
        assertThat(target.getRelasjonListe().getFirst().getRelasjonFraDato(), is(equalTo(LocalDate.of(1988, 1, 2))));
        assertThat(target.getRelasjonListe().getFirst().getDodsdato(), is(equalTo(LocalDate.of(2010, 1, 2))));
        assertThat(target.getRelasjonListe().getFirst().getHarVaertGift(), is(true));
        assertThat(target.getRelasjonListe().getFirst().getVarigAdskilt(), is(true));
    }

    @Test
    void mapAlderspensjonSkiltUtenPartner_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(1234)
                        .build()))
                .uttaksgrad(100)
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", Set.of("q2"));
        context.setProperty("pdlRelasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                                .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                                .type(PdlPerson.SivilstandType.GIFT)
                                                .relatertVedSivilstand("456")
                                                .build(),
                                        PdlPerson.Sivilstand.builder()
                                                .type(PdlPerson.SivilstandType.SKILT)
                                                .gyldigFraOgMed(LocalDate.of(2016, 1, 1))
                                                .build()))
                                .build())
                        .build(),
                PdlPersonBolk.PersonBolk.builder()
                        .ident("456")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("123")
                                        .build()))
                                .build())
                        .build()));
        context.setProperty("dollyRelasjoner", emptyList());

        var target = mapperFacade.map(pensjon, AlderspensjonSoknadRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
        assertThat(target.getSivilstand(), is(equalTo(AlderspensjonSoknadRequest.SivilstandType.SKIL)));
        assertThat(target.getSivilstandDatoFom(), is(equalTo(LocalDate.of(2016, 1, 1))));
        assertThat(target.getRelasjonListe().getFirst().getRelasjonType(), is(equalTo(AlderspensjonSoknadRequest.RelasjonType.EKTEF)));
        assertThat(target.getRelasjonListe().getFirst().getRelasjonFraDato(), is(equalTo(LocalDate.of(1988, 1, 2))));
        assertThat(target.getRelasjonListe().getFirst().getFnr(), is(equalTo("456")));
        assertThat(target.getRelasjonListe().getFirst().getSamlivsbruddDato(), is(equalTo(LocalDate.of(2016, 1, 1))));
    }

    @Test
    void sivilstandOrderSjekkMedDato_OK() {

        var sivilstand = List.of(PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.UGIFT)
                        .gyldigFraOgMed(LocalDate.of(1960, 1, 15))
                        .build(),
                PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.GIFT)
                        .gyldigFraOgMed(LocalDate.of(1986, 5, 8))
                        .build(),
                PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.SKILT)
                        .gyldigFraOgMed(LocalDate.of(2013, 10, 5))
                        .build());

        var resultat = sivilstand.stream()
                .sorted(new PensjonAlderspensjonSoknadMappingStrategy.SivilstandSort())
                .toList();

        assertThat(resultat.get(0).getType(), is(equalTo(PdlPerson.SivilstandType.SKILT)));
        assertThat(resultat.get(1).getType(), is(equalTo(PdlPerson.SivilstandType.GIFT)));
        assertThat(resultat.get(2).getType(), is(equalTo(PdlPerson.SivilstandType.UGIFT)));
    }

    @Test
    void sivilstandOrderSjekkUtenDato_OK() {

        var sivilstand = List.of(PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.UGIFT)
                        .build(),
                PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.GIFT)
                        .build(),
                PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.SKILT)
                        .build());

        var resultat = sivilstand.stream()
                .sorted(new PensjonAlderspensjonSoknadMappingStrategy.SivilstandSort())
                .toList();

        assertThat(resultat.get(0).getType(), is(equalTo(PdlPerson.SivilstandType.SKILT)));
        assertThat(resultat.get(1).getType(), is(equalTo(PdlPerson.SivilstandType.GIFT)));
        assertThat(resultat.get(2).getType(), is(equalTo(PdlPerson.SivilstandType.UGIFT)));
    }
}
