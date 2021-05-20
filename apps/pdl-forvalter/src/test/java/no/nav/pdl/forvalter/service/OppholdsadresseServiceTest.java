package no.nav.pdl.forvalter.service;

import ma.glasnost.orika.MapperFacade;
import no.nav.pdl.forvalter.domain.PdlMatrikkeladresse;
import no.nav.pdl.forvalter.domain.PdlOppholdsadresse;
import no.nav.pdl.forvalter.domain.PdlUtenlandskAdresse;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.pdl.forvalter.dto.PdlAdresseResponse.Vegadresse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static no.nav.pdl.forvalter.domain.PdlAdresse.Master.FREG;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OppholdsadresseServiceTest {

    @Mock
    private VegadresseService vegadresseService;

    @Mock
    private MapperFacade mapperFacade;

    @InjectMocks
    private OppholdsadresseService oppholdsadresseService;

    @Test
    void whenMultipleAdressesProvided_thenThrowExecption() {

        var request = List.of(PdlOppholdsadresse.builder()
                .vegadresse(new PdlVegadresse())
                .matrikkeladresse(new PdlMatrikkeladresse())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert((List<PdlOppholdsadresse>) request));

        assertThat(exception.getMessage(), containsString("Kun én adresse skal være satt " +
                "(vegadresse, matrikkeladresse, utenlandskAdresse)"));
    }

    @Test
    void whenNoAdressProvided_thenThrowExecption() {

        var request =List.of(PdlOppholdsadresse.builder()
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert((List<PdlOppholdsadresse>) request));

        assertThat(exception.getMessage(), containsString("Én av adressene må velges " +
                "(vegadresse, matrikkeladresse, utenlandskAdresse)"));
    }

    @Test
    void whenUtenlandskAdresseProvidedAndMasterIsFreg_thenThrowExecption() {

        var request = List.of(PdlOppholdsadresse.builder()
                .utenlandskAdresse(new PdlUtenlandskAdresse())
                .master(FREG)
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert((List<PdlOppholdsadresse>) request));

        assertThat(exception.getMessage(), containsString("Utenlandsk adresse krever at master er PDL"));
    }

    @Test
    void whenVegadresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request = List.of(PdlOppholdsadresse.builder()
                .vegadresse(PdlVegadresse.builder()
                        .bruksenhetsnummer("HK25419")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert((List<PdlOppholdsadresse>) request));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenMatrikkeladresseWithBruksenhetsnummerInvalidFormat_thenThrowExecption() {

        var request =List.of(PdlOppholdsadresse.builder()
                .matrikkeladresse(PdlMatrikkeladresse.builder()
                        .bruksenhetsnummer("F8021")
                        .build())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert((List<PdlOppholdsadresse>) request));

        assertThat(exception.getMessage(), containsString("Gyldig format er Bokstaven H, L, U eller K etterfulgt av fire sifre"));
    }

    @Test
    void whenInvalidDateInterval_thenThrowExecption() {

        var request = List.of(PdlOppholdsadresse.builder()
                .vegadresse(new PdlVegadresse())
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2018, 1, 1).atStartOfDay())
                .isNew(true)
                .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert((List<PdlOppholdsadresse>) request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput_thenThrowExecption() {

        var request = List.of(PdlOppholdsadresse.builder()
                        .vegadresse(new PdlVegadresse())
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 2).atStartOfDay())
                        .isNew(true)
                        .build(),
                PdlOppholdsadresse.builder()
                        .matrikkeladresse(new PdlMatrikkeladresse())
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .isNew(true)
                        .build());

        when(vegadresseService.get(any(PdlVegadresse.class), isNull())).thenReturn(new Vegadresse());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert(request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenOverlappingDateIntervalsInInput2_thenThrowExecption() {

        var request = List.of(PdlOppholdsadresse.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 2, 3).atStartOfDay())
                        .matrikkeladresse(new PdlMatrikkeladresse())
                        .isNew(true)
                        .build(),
                PdlOppholdsadresse.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .gyldigTilOgMed(LocalDate.of(2020, 2, 3).atStartOfDay())
                        .utenlandskAdresse(new PdlUtenlandskAdresse())
                        .isNew(true)
                        .build());

        var exception = assertThrows(HttpClientErrorException.class, () ->
                oppholdsadresseService.convert(request));

        assertThat(exception.getMessage(), containsString("Feil: Overlappende adressedatoer er ikke lov"));
    }

    @Test
    void whenFraDatoAndEmptyTilDato_thenAcceptRequest() {

        var target = oppholdsadresseService.convert(List.of(PdlOppholdsadresse.builder()
                .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                .matrikkeladresse(new PdlMatrikkeladresse())
                .isNew(true)
                .build())).get(0);

        assertThat(target.getGyldigFraOgMed(), is(equalTo(LocalDate.of(2020, 1, 1).atStartOfDay())));
    }

    @Test
    void whenPreviousOppholdHasEmptyTilDato_thenFixPreviousOppholdTilDato() {

        when(vegadresseService.get(any(PdlVegadresse.class), isNull())).thenReturn(new Vegadresse());

        var target = oppholdsadresseService.convert(List.of(PdlOppholdsadresse.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 2, 4).atStartOfDay())
                        .vegadresse(new PdlVegadresse())
                        .isNew(true)
                        .build(),
                PdlOppholdsadresse.builder()
                        .gyldigFraOgMed(LocalDate.of(2020, 1, 1).atStartOfDay())
                        .utenlandskAdresse(new PdlUtenlandskAdresse())
                        .isNew(true)
                        .build()));

        assertThat(target.get(1).getGyldigTilOgMed(), is(equalTo(LocalDate.of(2020, 2, 3).atStartOfDay())));
    }
}