package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.DeltBostedDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.BARN;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FAR;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.FORELDER;
import static no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO.Rolle.MOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ForelderBarnRelasjonServiceTest {

    private static final String IDENT = "12345678901";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private ForelderBarnRelasjonService forelderBarnRelasjonService;

    @Test
    void whenMinRolleForPersonIsMissing_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                forelderBarnRelasjonService.validate(request));

        assertThat(exception.getMessage(), containsString("ForelderBarnRelasjon: min rolle for person må oppgis"));
    }

    @Test
    void whenRelatertRolleForPersonIsMissing_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FAR)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                forelderBarnRelasjonService.validate(request));

        assertThat(exception.getMessage(), containsString("ForelderBarnRelasjon: relatert persons rolle må oppgis"));
    }

    @Test
    void whenAmbiguousRollerForelderIsGiven_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(FAR)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                forelderBarnRelasjonService.validate(request));

        assertThat(exception.getMessage(), containsString("ForelderBarnRelasjon: min rolle og relatert persons rolle " +
                "må være av type barn -- forelder, eller forelder -- barn"));
    }

    @Test
    void whenAmbiguousRollerBarnIsGiven_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(BARN)
                .relatertPersonsRolle(BARN)
                .isNew(true)
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                forelderBarnRelasjonService.validate(request));

        assertThat(exception.getMessage(), containsString("ForelderBarnRelasjon: min rolle og relatert persons rolle " +
                "må være av type barn -- forelder, eller forelder -- barn"));
    }

    @Test
    void whenNonExistingPersonStated_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(MOR)
                .relatertPersonsRolle(BARN)
                .relatertPerson(IDENT)
                .isNew(true)
                .build();

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var exception = assertThrows(HttpClientErrorException.class, () ->
                forelderBarnRelasjonService.validate(request));

        assertThat(exception.getMessage(),
                containsString(String.format("ForelderBarnRelasjon: Relatert person %s finnes ikke", IDENT)));
    }

    @Test
    void whenAmbiguosAdresserExist_thenThrowExecption() {

        var request = ForelderBarnRelasjonDTO.builder()
                .minRolleForPerson(FORELDER)
                .relatertPersonsRolle(BARN)
                .deltBosted(DeltBostedDTO.builder()
                        .vegadresse(new VegadresseDTO())
                        .matrikkeladresse(new MatrikkeladresseDTO())
                        .startdatoForKontrakt(LocalDateTime.now())
                        .isNew(true)
                        .build())
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                forelderBarnRelasjonService.validate(request));

        assertThat(exception.getMessage(), containsString("Delt bosted: kun én adresse skal være satt (vegadresse, ukjentBosted, matrikkeladresse)"));
    }
}