package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class DeltBostedServiceTest {

    @InjectMocks
    private DeltBostedService deltBostedService;

    @Test
    void whenBarnIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .deltBosted(List.of(DeltBostedDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .startdatoForKontrakt(LocalDateTime.now())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                deltBostedService.convert(request));

        assertThat(exception.getMessage(), containsString("Delt bosted: det finnes ingen barn å knytte delt bosted til"));
    }

    @Test
    void whenStartDatoForKontraktIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .forelderBarnRelasjon(List.of(ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(FORELDER)
                        .relatertPersonsRolle(BARN)
                        .build()))
                .deltBosted(List.of(DeltBostedDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                deltBostedService.convert(request));

        assertThat(exception.getMessage(), containsString("Delt bosted: startdato for kontrakt må angis"));
    }

    @Test
    void whenAmbiguosAdresserExist_thenThrowExecption() {

        var request = PersonDTO.builder()
                .forelderBarnRelasjon(List.of(ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(FORELDER)
                        .relatertPersonsRolle(BARN)
                        .build()))
                .deltBosted(List.of(DeltBostedDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .matrikkeladresse(new MatrikkeladresseDTO())
                        .startdatoForKontrakt(LocalDateTime.now())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                deltBostedService.convert(request));

        assertThat(exception.getMessage(), containsString("Delt bosted: kun én adresse skal være satt (vegadresse, ukjentBosted, matrikkeladresse)"));
    }

    @Test
    void whenNoAdresseExists_thenThrowExecption() {

        var request = PersonDTO.builder()
                .forelderBarnRelasjon(List.of(ForelderBarnRelasjonDTO.builder()
                        .minRolleForPerson(FORELDER)
                        .relatertPersonsRolle(BARN)
                        .build()))
                .deltBosted(List.of(DeltBostedDTO.builder()
                        .startdatoForKontrakt(LocalDateTime.now())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                deltBostedService.convert(request));

        assertThat(exception.getMessage(), containsString("Delt bosted: når personen ikke er gift må en adresse oppgis"));
    }
}