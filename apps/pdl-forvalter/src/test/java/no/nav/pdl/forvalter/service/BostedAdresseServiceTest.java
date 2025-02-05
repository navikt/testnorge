package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.BostedadresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.DbVersjonDTO.Master;
import no.nav.testnav.libs.data.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UkjentBostedDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.UtenlandskAdresseDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.VegadresseDTO;
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

import static no.nav.testnav.libs.data.pdlforvalter.v1.AdressebeskyttelseDTO.AdresseBeskyttelse.STRENGT_FORTROLIG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

        var request = BostedadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .matrikkeladresse(new MatrikkeladresseDTO())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.validate(request, new PersonDTO()));

        assertThat(exception.getMessage(), containsString("én adresse skal være satt (vegadresse, " +
                "matrikkeladresse, ukjentbosted, utenlandskAdresse)"));
    }

    @Test
    void whenUtenlandskAdresseProvidedAndMasterIsFreg_thenThrowExecption() {

        var request = BostedadresseDTO.builder()
                .utenlandskAdresse(new UtenlandskAdresseDTO())
                .master(Master.FREG)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("utenlandsk adresse krever at master er PDL"));
    }

    @Test
    void whenVegadresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder()
                        .bruksenhetsnummer("HK25419")
                        .build())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenMatrikkeladresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = BostedadresseDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder()
                        .bruksenhetsnummer("F8021")
                        .build())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = BostedadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.validate(request, PersonDTO.builder()
                        .ident(FNR_IDENT)
                        .build()));

        assertThat(exception.getMessage(), containsString("Adresse: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenOverlappingGyldigTil_thenFixInterval() {

        when(adresseServiceConsumer.getMatrikkeladresse(any(MatrikkeladresseDTO.class), any()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO());

        var request = PersonDTO.builder()
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
                .build();

        var response = bostedAdresseService.convert(request, null);

        assertThat(response.get(1).getGyldigTilOgMed(), is(equalTo(LocalDateTime.of(2021, 2, 1, 0, 0))));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .ukjentBosted(new UkjentBostedDTO())
                        .isNew(true)
                        .build())))
                .build();

        var target = bostedAdresseService.convert(request, null).getFirst();

        assertThat(target.getGyldigFraOgMed(), is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay())));
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), isNull())).thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO());

        var request = PersonDTO.builder()
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
                .build();

        var target = bostedAdresseService.convert(request, null);

        assertThat(target.get(1).getGyldigTilOgMed(), is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
    }

    @Test
    void whenIdenttypeFnrAndStrengtFortrolig_thenMakeNoAdress() {

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                        .isNew(true)
                        .build())))
                .adressebeskyttelse(List.of(AdressebeskyttelseDTO.builder()
                        .gradering(STRENGT_FORTROLIG)
                        .build()))
                .build();

        var target = bostedAdresseService.convert(request, null);

        assertThat(target, is(empty()));
    }

    @Test
    void whenIdenttypeFnrAndNoAdresseBeskyttelse_thenMakeAdress() {

        var request = PersonDTO.builder()
                .ident(FNR_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                        .isNew(true)
                        .build())))
                .build();

        when(adresseServiceConsumer.getVegadresse(any(VegadresseDTO.class), any()))
                .thenReturn(new no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO());

        var target = bostedAdresseService.convert(request, null).getFirst();

        assertThat(target.countAdresser(), is(1));
        assertThat(target.getVegadresse(), is(notNullValue()));
    }

    @Test
    void whenIdenttypeDNr_thenMakeUtenlandskAdresse() {

        var request = PersonDTO.builder()
                .ident(DNR_IDENT)
                .bostedsadresse(new ArrayList<>(List.of(BostedadresseDTO.builder()
                        .isNew(true)
                        .build())))
                .build();

        when(enkelAdresseService.getUtenlandskAdresse(any(), any(), any())).thenReturn(new UtenlandskAdresseDTO());

        var target = bostedAdresseService.convert(request, null).getFirst();

        assertThat(target.countAdresser(), is(1));
        assertThat(target.getUtenlandskAdresse(), is(notNullValue()));
    }
}