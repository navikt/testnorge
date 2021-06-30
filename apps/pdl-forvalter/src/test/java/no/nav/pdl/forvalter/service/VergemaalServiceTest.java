package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.VergemaalDTO;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.VergemaalEmbete;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.VergemaalSakstype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VergemaalServiceTest {

    private static final String IDENT = "12345678901";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private VergemaalService vergemaalService;

    @Test
    void whenEmbeteIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .vergemaal(List.of(VergemaalDTO.builder()
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                vergemaalService.convert(request));

        assertThat(exception.getMessage(), containsString("Embete for vergemål må angis"));
    }

    @Test
    void whenTypeIsMissing_thenThrowExecption() {

        var request = PersonDTO.builder()
                .vergemaal(List.of(VergemaalDTO.builder()
                        .vergemaalEmbete(VergemaalEmbete.FMAV)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                vergemaalService.convert(request));

        assertThat(exception.getMessage(), containsString("Sakstype av vergemål må angis"));
    }

    @Test
    void whenUgyldigDatoInterval_thenThrowExecption() {

        var request = PersonDTO.builder()
                .vergemaal(List.of(VergemaalDTO.builder()
                        .vergemaalEmbete(VergemaalEmbete.FMIN)
                        .gyldigFom(LocalDate.of(2012, 04, 05).atStartOfDay())
                        .gyldigTom(LocalDate.of(2012, 04, 04).atStartOfDay())
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                vergemaalService.convert(request));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenStatedPersonDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var request = PersonDTO.builder()
                .vergemaal(List.of(VergemaalDTO.builder()
                        .vergemaalEmbete(VergemaalEmbete.FMNO)
                        .sakType(VergemaalSakstype.EMF)
                        .vergeIdent(IDENT)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                vergemaalService.convert(request));

        assertThat(exception.getMessage(), containsString(format("Vergeperson med ident %s ikke funnet i database", IDENT)));
    }
}