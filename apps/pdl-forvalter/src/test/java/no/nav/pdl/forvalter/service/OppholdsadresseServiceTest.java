package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.OppholdsadresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OppholdsadresseServiceTest {

    private static final String FNR_IDENT = "12044512345";
    private static final String DNR_IDENT = "45027812345";

    @Mock
    private AdresseServiceConsumer adresseServiceConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private DummyAdresseService dummyAdresseService;

    @InjectMocks
    private OppholdsadresseService oppholdsadresseService;

    @Test
    void whenMultipleAdressesProvided_thenThrowExecption() {

        var request = OppholdsadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .matrikkeladresse(new MatrikkeladresseDTO())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(), containsString("kun én adresse skal være satt " +
                "(vegadresse, matrikkeladresse, utenlandskAdresse)"));
    }

    @Test
    void whenAddressProvidedAndStrengtFortrolig_thenThrowExecption() {

        var request = OppholdsadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                                .gradering(STRENGT_FORTROLIG)
                                .build()))
                        .build()));

        assertThat(exception.getMessage(), containsString("" +
                "Oppholdsadresse: Personer med adressebeskyttelse == STRENGT_FORTROLIG skal ikke ha oppholdsadresse"));
    }

    @Test
    void whenVegadresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = OppholdsadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder()
                        .bruksenhetsnummer("HK25419")
                        .build())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenMatrikkeladresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = OppholdsadresseDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder()
                        .bruksenhetsnummer("F8021")
                        .build())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = OppholdsadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Adresse: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenPartialDayBetweenDates_AcceptInput() {

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), isNull()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO());
        when(adresseServiceConsumer.getMatrikkeladresse(any(MatrikkeladresseDTO.class), any()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO());

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                                .vegadresse(new VegadresseDTO())
                                .gyldigFraOgMed(LocalDate.of(2020, 1, 2).atTime(15, 0, 0))
                                .isNew(true)
                                .build(),
                        OppholdsadresseDTO.builder()
                                .matrikkeladresse(new MatrikkeladresseDTO())
                                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atTime(16, 0, 0))
                                .isNew(true)
                                .build())))
                .build();

        var response = oppholdsadresseService.convert(request);

        assertThat(response.get(1).getGyldigTilOgMed(), is(equalTo(LocalDateTime.of(2020, 1, 2, 14, 59, 59))));
    }

    @Test
    void whenOverlappingGyldigTil_thenFixIt() {

        when(adresseServiceConsumer.getMatrikkeladresse(any(MatrikkeladresseDTO.class), any()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO());

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .gyldigTilOgMed(LocalDate.of(2021, 2, 3).atStartOfDay())
                                .utenlandskAdresse(new UtenlandskAdresseDTO())
                                .isNew(true)
                                .build(),
                        OppholdsadresseDTO.builder()
                                .gyldigFraOgMed(LocalDate.of(2020, 2, 3).atStartOfDay())
                                .matrikkeladresse(new MatrikkeladresseDTO())
                                .isNew(true)
                                .build())))
                .build();

        var response = oppholdsadresseService.convert(request);

        assertThat(response.get(1).getGyldigTilOgMed(), is(equalTo(LocalDate.of(2020, 2, 2).atStartOfDay())));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        when(adresseServiceConsumer.getMatrikkeladresse(any(MatrikkeladresseDTO.class), any()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO());

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .matrikkeladresse(new MatrikkeladresseDTO())
                        .isNew(true)
                        .build())))
                .build();

        var target = oppholdsadresseService.convert(request).get(0);

        assertThat(target.getGyldigFraOgMed(), is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay())));
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), isNull())).thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO());

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                                .gyldigFraOgMed(LocalDate.of(2020, 2, 4).atStartOfDay())
                                .vegadresse(new VegadresseDTO())
                                .isNew(true)
                                .build(),
                        OppholdsadresseDTO.builder()
                                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                                .utenlandskAdresse(new UtenlandskAdresseDTO())
                                .isNew(true)
                                .build())))
                .build();

        var target = oppholdsadresseService.convert(request);

        assertThat(target.get(1).getGyldigTilOgMed(), is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
    }

    @Test
    void whenIdenttypeFnrAndStrengtFortrolig_thenMakeNoAdress() {

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                        .isNew(true)
                        .build())))
                .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                        .gradering(STRENGT_FORTROLIG)
                        .build()))
                .build();

        var target = oppholdsadresseService.convert(request).get(0);

        assertThat(target.countAdresser(), is(0));
    }

    @Test
    void whenIdenttypeFnrAndNoAdresseBeskyttelse_thenMakeAdress() {

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                        .isNew(true)
                        .build())))
                .build();

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), any()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO());

        var target = oppholdsadresseService.convert(request).get(0);

        assertThat(target.countAdresser(), is(1));
        assertThat(target.getVegadresse(), is(notNullValue()));
    }

    @Test
    void whenUtenlandskAdresse_thenMakeUtenlandskAdresse() {

        var request = PersonDTO.builder()
                .ident(DNR_IDENT)
                .oppholdsadresse(new ArrayList<>(List.of(OppholdsadresseDTO.builder()
                        .isNew(true)
                        .utenlandskAdresse(new UtenlandskAdresseDTO())
                        .build())))
                .build();

        when(dummyAdresseService.getUtenlandskAdresse(any())).thenReturn(new UtenlandskAdresseDTO());

        var target = oppholdsadresseService.convert(request).get(0);

        assertThat(target.countAdresser(), is(1));
        assertThat(target.getUtenlandskAdresse(), is(notNullValue()));
    }
}