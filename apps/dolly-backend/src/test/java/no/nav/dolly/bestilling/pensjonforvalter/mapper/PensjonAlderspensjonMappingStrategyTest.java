package no.nav.dolly.bestilling.pensjonforvalter.mapper;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.mapper.strategy.LocalDateCustomMapping;
import no.nav.dolly.mapper.utils.MapperTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class PensjonAlderspensjonMappingStrategyTest {


    private MapperFacade mapperFacade;

    @BeforeEach
    void setup() {
        mapperFacade = MapperTestUtils.createMapperFacadeForMappingStrategy(new LocalDateCustomMapping(),
                new PensjonAlderspensjonMappingStrategy());
    }

    @Test
    void mapAlderspensjonUtenPartner_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", List.of("q2"));
        context.setProperty("relasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                .ident("123")
                .person(PdlPerson.Person.builder()
                        .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                .type(PdlPerson.SivilstandType.SKILT_PARTNER)
                                .build()))
                        .build())
                .build()));

        var target = mapperFacade.map(pensjon, AlderspensjonRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getSivilstand(), is(equalTo("SKPA")));
        assertThat(target.getSivilstandDatoFom(), is(equalTo(LocalDate.of(1988, 1, 2))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));
    }

    @Test
    void mapAlderspensjonMedPartner_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(100000)
                        .build()))
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", List.of("q2"));
        context.setProperty("relasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
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
                                        .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("123")
                                        .build()))
                                .build())
                        .build()));

        var target = mapperFacade.map(pensjon, AlderspensjonRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getSivilstand(), is(equalTo("GIFT")));
        assertThat(target.getSivilstandDatoFom(), is(equalTo(LocalDate.of(1988, 1, 2))));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));

        assertThat(target.getRelasjonListe().get(0).getFnr(), is(equalTo("456")));
        assertThat(target.getRelasjonListe().get(0).getSumAvForventetArbeidKapitalPensjonInntekt(), is(equalTo(100000)));
        assertThat(target.getRelasjonListe().get(0).getRelasjonType(), is(equalTo(PensjonAlderspensjonMappingStrategy.RelasjonType.EKTEF.name())));
        assertThat(target.getRelasjonListe().get(0).getRelasjonFraDato(), is(equalTo(LocalDate.of(1988, 1, 2))));
        assertThat(target.getRelasjonListe().get(0).getHarVaertGift(), is(equalTo(true)));
        assertThat(target.getRelasjonListe().get(0).getVarigAdskilt(), is(equalTo(false)));
        assertThat(target.getRelasjonListe().get(0).getSamlivsbruddDato(), is(nullValue()));
        assertThat(target.getRelasjonListe().get(0).getHarFellesBarn(), is(equalTo(false)));
    }

    @Test
    void mapAlderspensjonSkiltUtenPartner_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .iverksettelsesdato(LocalDate.of(2023, 1, 11))
                .uttaksgrad(100)
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(100000)
                        .build()))
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("miljoer", List.of("q2"));
        context.setProperty("relasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                                .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                                .type(PdlPerson.SivilstandType.GIFT)
                                                .relatertVedSivilstand("456")
                                                .build(),
                                        PdlPerson.Sivilstand.builder()
                                                .type(PdlPerson.SivilstandType.SKILT)
                                                .build()))
                                .build())
                        .build(),
                PdlPersonBolk.PersonBolk.builder()
                        .ident("456")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(2016, 1, 1))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("123")
                                        .build()))
                                .build())
                        .build()));

        var target = mapperFacade.map(pensjon, AlderspensjonRequest.class, context);

        assertThat(target.getIverksettelsesdato(), is(equalTo(LocalDate.of(2023, 1, 11))));
        assertThat(target.getSivilstand(), is(equalTo("SKIL")));
        assertThat(target.getSivilstandDatoFom(), is(nullValue()));
        assertThat(target.getUttaksgrad(), is(equalTo(100)));

        assertThat(target.getFnr(), is(equalTo("123")));
        assertThat(target.getMiljoer(), hasItem("q2"));

        assertThat(target.getRelasjonListe(), is(emptyList()));
    }

    @Test
    void mapAlderspensjonMedPartnerOgFellesBarn_OK() {

        var pensjon = PensjonData.Alderspensjon.builder()
                .relasjoner(List.of(PensjonData.SkjemaRelasjon.builder()
                        .sumAvForvArbKapPenInntekt(100000)
                        .build()))
                .build();
        var context = new MappingContext.Factory().getContext();
        context.setProperty("ident", "123");
        context.setProperty("relasjoner", List.of(PdlPersonBolk.PersonBolk.builder()
                        .ident("123")
                        .person(PdlPerson.Person.builder()
                                .sivilstand(List.of(PdlPerson.Sivilstand.builder()
                                        .gyldigFraOgMed(LocalDate.of(1988, 1, 2))
                                        .type(PdlPerson.SivilstandType.GIFT)
                                        .relatertVedSivilstand("456")
                                        .build()))
                                .forelderBarnRelasjon(List.of(PdlPerson.ForelderBarnRelasjon.builder()
                                        .relatertPersonsRolle(PdlPerson.Rolle.BARN)
                                        .relatertPersonsIdent("891")
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
                                        .relatertPersonsRolle(PdlPerson.Rolle.BARN)
                                        .relatertPersonsIdent("891")
                                        .build()))
                                .build())
                        .build()));

        var target = mapperFacade.map(pensjon, AlderspensjonRequest.class, context);

        assertThat(target.getRelasjonListe().get(0).getFnr(), is(equalTo("456")));
        assertThat(target.getRelasjonListe().get(0).getSumAvForventetArbeidKapitalPensjonInntekt(), is(equalTo(100000)));
        assertThat(target.getRelasjonListe().get(0).getRelasjonType(), is(equalTo(PensjonAlderspensjonMappingStrategy.RelasjonType.EKTEF.name())));
        assertThat(target.getRelasjonListe().get(0).getRelasjonFraDato(), is(equalTo(LocalDate.of(1988, 1, 2))));
        assertThat(target.getRelasjonListe().get(0).getHarVaertGift(), is(equalTo(true)));
        assertThat(target.getRelasjonListe().get(0).getHarFellesBarn(), is(equalTo(true)));
    }

    @Test
    void sivilstandOrderSjekkMedDato_OK() {

        var sivilstand = List.of(PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.UGIFT)
                        .gyldigFraOgMed(LocalDate.of(1960,1,15))
                        .build(),
                PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.GIFT)
                        .gyldigFraOgMed(LocalDate.of(1986,5,8))
                        .build(),
                PdlPerson.Sivilstand.builder()
                        .type(PdlPerson.SivilstandType.SKILT)
                        .gyldigFraOgMed(LocalDate.of(2013, 10,5))
                        .build());

        var resultat = sivilstand.stream()
                .sorted(new PensjonAlderspensjonMappingStrategy.SivilstandSort())
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
                .sorted(new PensjonAlderspensjonMappingStrategy.SivilstandSort())
                .toList();

        assertThat(resultat.get(0).getType(), is(equalTo(PdlPerson.SivilstandType.SKILT)));
        assertThat(resultat.get(1).getType(), is(equalTo(PdlPerson.SivilstandType.GIFT)));
        assertThat(resultat.get(2).getType(), is(equalTo(PdlPerson.SivilstandType.UGIFT)));
    }
}
