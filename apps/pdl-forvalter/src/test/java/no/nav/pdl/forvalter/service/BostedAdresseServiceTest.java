package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.consumer.AdresseServiceConsumer;
import no.nav.pdl.forvalter.domain.BostedadresseDTO;
import no.nav.pdl.forvalter.domain.BostedadresseDTO.UkjentBostedDTO;
import no.nav.pdl.forvalter.domain.MatrikkeladresseDTO;
import no.nav.pdl.forvalter.domain.UtenlandskAdresseDTO;
import no.nav.pdl.forvalter.domain.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static no.nav.pdl.forvalter.domain.AdresseDTO.Master.FREG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BostedAdresseServiceTest {

    @Mock
    private AdresseServiceConsumer adresseServiceConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private BostedAdresseService bostedAdresseService;

    @Test
    void whenMultipleAdressesProvided_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .matrikkeladresse(new MatrikkeladresseDTO())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert((List<BostedadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Kun én adresse skal være satt (vegadresse, " +
                "matrikkeladresse, ukjentbosted, utenlandskAdresse)"));
    }

    @Test
    void whenNoAdressProvided_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert((List<BostedadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Én av adressene må velges " +
                "(vegadresse, matrikkeladresse, ukjentbosted, utenlandskAdresse)"));
    }

    @Test
    void whenUtenlandskAdresseProvidedAndMasterIsFreg_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                .utenlandskAdresse(new UtenlandskAdresseDTO())
                .master(FREG)
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert((List<BostedadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Utenlandsk adresse krever at master er PDL"));
    }

    @Test
    void whenVegadresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                .vegadresse(VegadresseDTO.builder()
                        .bruksenhetsnummer("HK25419")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert((List<BostedadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenMatrikkeladresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                .matrikkeladresse(MatrikkeladresseDTO.builder()
                        .bruksenhetsnummer("F8021")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert((List<BostedadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                .vegadresse(new VegadresseDTO())
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert((List<BostedadresseDTO>) request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 2).atStartOfDay())
                        .isNew(true)
                        .build(),
                BostedadresseDTO.builder()
                        .matrikkeladresse(new MatrikkeladresseDTO())
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .isNew(true)
                        .build());

        when(adresseServiceConsumer.getAdresse(any(VegadresseDTO.class), isNull())).thenReturn(new no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert(request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput2_thenThrowExecption() {

        var request = List.of(BostedadresseDTO.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 2, 3).atStartOfDay())
                        .matrikkeladresse(new MatrikkeladresseDTO())
                        .isNew(true)
                        .build(),
                BostedadresseDTO.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .gyldigTilOgMed(LocalDate.of(2020, 2, 3).atStartOfDay())
                        .utenlandskAdresse(new UtenlandskAdresseDTO())
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                bostedAdresseService.convert(request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        var target = bostedAdresseService.convert(List.of(BostedadresseDTO.builder()
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .ukjentBosted(new UkjentBostedDTO())
                .isNew(true)
                .build())).get(0);

        assertThat(target.getGyldigFraOgMed(), is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay())));
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        when(adresseServiceConsumer.getAdresse(any(VegadresseDTO.class), isNull())).thenReturn(new no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO());

        var target = bostedAdresseService.convert(List.of(BostedadresseDTO.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 2, 4).atStartOfDay())
                        .vegadresse(new VegadresseDTO())
                        .isNew(true)
                        .build(),
                BostedadresseDTO.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .utenlandskAdresse(new UtenlandskAdresseDTO())
                        .isNew(true)
                        .build()));

        assertThat(target.get(1).getGyldigTilOgMed(), is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
    }
}