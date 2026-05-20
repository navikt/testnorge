package no.nav.pdl.forvalter.service;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.database.model.DbPerson;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UkjentBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BostedAdresseServiceTest {

    private static final String FNR_IDENT = "12044512345";
    private static final String DNR_IDENT = "45027812345";

    @Mock
    private AdresseServiceConsumer adresseServiceConsumer;

    @Mock
    private EnkelAdresseService enkelAdresseService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private BostedAdresseService bostedAdresseService;

    @Test
    void whenMultipleAdressesProvided_thenThrowExecption() {

        val request = BostedadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .matrikkeladresse(new MatrikkeladresseDTO())
                .isNew(true)
                .build();

        StepVerifier.create(bostedAdresseService.validate(request, new PersonDTO()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("én adresse skal være satt (vegadresse, matrikkeladresse, ukjentbosted, utenlandskAdresse)"));
    }

    @Test
    void whenUtenlandskAdresseProvidedAndMasterIsFreg_thenThrowExecption() {

        val request = BostedadresseDTO.builder()
                .utenlandskAdresse(new UtenlandskAdresseDTO())
                .master(Master.FREG)
                .isNew(true)
                .build();

        StepVerifier.create(bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("utenlandsk adresse krever at master er PDL"));
    }

    @Test
    void whenVegadresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        val request = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder()
                        .bruksenhetsnummer("HK25419")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenMatrikkeladresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        val request = BostedadresseDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder()
                        .bruksenhetsnummer("F8021")
                        .build())
                .isNew(true)
                .build();

        StepVerifier.create(bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        val request = BostedadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build();

        StepVerifier.create(bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()))
                .verifyErrorSatisfies(throwable ->
                        Assertions.assertThat(throwable).isInstanceOf(HttpClientErrorException.class)
                                .hasMessageContaining("Adresse: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenOverlappingGyldigTil_thenFixInterval() {

        when(adresseServiceConsumer.getMatrikkeladresse(any(MatrikkeladresseDTO.class), any()))
                .thenReturn(Mono.just(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO()));

        when(enkelAdresseService.getUtenlandskAdresse(any(UtenlandskAdresseDTO.class), isNull(), any(Master.class)))
                .thenReturn(Mono.just(new UtenlandskAdresseDTO()));

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(
                                BostedadresseDTO.builder()
                                        .gyldigFraOgMed(LocalDate.of(2021, 2, 2).atStartOfDay())
                                        .matrikkeladresse(new MatrikkeladresseDTO())
                                        .isNew(true)
                                        .build(),
                                BostedadresseDTO.builder()
                                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                                        .utenlandskAdresse(new UtenlandskAdresseDTO())
                                        .isNew(true)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(bostedAdresseService.convert(request, null))
                .assertNext(response ->
                        assertThat(response.getPerson().getBostedsadresse().getFirst().getGyldigTilOgMed(),
                                is(nullValue())))
                .verifyComplete();
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .ukjentBosted(new UkjentBostedDTO())
                                .isNew(true)
                                .build())))
                        .build())
                .build();

        StepVerifier.create(bostedAdresseService.convert(request, null))
                .assertNext(target ->
                        assertThat(target.getPerson().getBostedsadresse().getFirst().getGyldigFraOgMed(),
                                is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay()))))
                .verifyComplete();
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), isNull()))
                .thenReturn(Mono.just(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO()));

        when(enkelAdresseService.getUtenlandskAdresse(any(UtenlandskAdresseDTO.class), isNull(), any(Master.class)))
                .thenReturn(Mono.just(new UtenlandskAdresseDTO()));

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                                        .gyldigFraOgMed(LocalDate.of(2020, 2, 4).atStartOfDay())
                                        .vegadresse(new VegadresseDTO())
                                        .isNew(true)
                                        .build(),
                                BostedadresseDTO.builder()
                                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                                        .utenlandskAdresse(new UtenlandskAdresseDTO())
                                        .isNew(true)
                                        .build())))
                        .build())
                .build();

        StepVerifier.create(bostedAdresseService.convert(request, null))
                .assertNext(target ->
                        assertThat(target.getPerson().getBostedsadresse().getFirst().getGyldigTilOgMed(),
                                is(nullValue())))
                .verifyComplete();
    }

    @Test
    void whenIdenttypeFnrAndStrengtFortrolig_thenMakeNoAdress() {

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                                .isNew(true)
                                .build())))
                        .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                .gradering(STRENGT_FORTROLIG)
                                .build()))
                        .build())
                .build();

        StepVerifier.create(bostedAdresseService.convert(request, null))
                .assertNext(target -> assertThat(target.getPerson().getBostedsadresse(), is(empty())))
                .verifyComplete();
    }

    @Test
    void whenIdenttypeFnrAndNoAdresseBeskyttelse_thenMakeAdress() {

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                                .isNew(true)
                                .build())))
                        .build())
                .build();

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), isNull()))
                .thenReturn(Mono.just(no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO.builder()
                        .matrikkelId("123456789")
                        .build()));

        StepVerifier.create(bostedAdresseService.convert(request, null))
                .assertNext(target -> {

                    assertThat(target.getPerson().getBostedsadresse().getFirst().countAdresser(), is(1));
                    assertThat(target.getPerson().getBostedsadresse().getFirst().getVegadresse(), is(notNullValue()));
                    assertThat(target.getPerson().getBostedsadresse().getFirst().getAdresseIdentifikatorFraMatrikkelen(), is(equalTo("123456789")));
                })
                .verifyComplete();
    }

    @Test
    void whenIdenttypeDNr_thenMakeUtenlandskAdresse() {

        val request = DbPerson.builder()
                .person(PersonDTO.builder()
                        .ident(DNR_IDENT)
                        .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                                .isNew(true)
                                .build())))
                        .build())
                .build();

        when(enkelAdresseService.getUtenlandskAdresse(any(), any(), any())).thenReturn(Mono.just(new UtenlandskAdresseDTO()));

        StepVerifier.create(bostedAdresseService.convert(request, null))
                .assertNext(target -> {

                    assertThat(target.getPerson().getBostedsadresse().getFirst().countAdresser(), is(1));
                    assertThat(target.getPerson().getBostedsadresse().getFirst().getUtenlandskAdresse(), is(notNullValue()));
                })
                .verifyComplete();
    }
}