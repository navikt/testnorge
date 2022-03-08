package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.FullmaktDTO;
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
class FullmaktServiceTest {

    private static final String IDENT = "12345678901";

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private FullmaktService fullmaktService;

    @Test
    void whenOmraaderIsMissing_thenThrowExecption() {

        var request = FullmaktDTO.builder()
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                fullmaktService.validate(request));

        assertThat(exception.getMessage(), containsString("Områder for fullmakt må angis"));
    }

    @Test
    void whenGyldigFomIsMissing_thenThrowExecption() {

        var request = FullmaktDTO.builder()
                        .omraader(List.of("OMRAADE"))
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                fullmaktService.validate(request));

        assertThat(exception.getMessage(), containsString("Fullmakt med gyldigFom må angis"));
    }

    @Test
    void whenGyldigTomIsMissing_thenThrowExecption() {

        var request = FullmaktDTO.builder()
                        .omraader(List.of("OMRAADE"))
                        .gyldigFraOgMed(LocalDate.of(2012, 04, 05).atStartOfDay())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                fullmaktService.validate(request));

        assertThat(exception.getMessage(), containsString("Fullmakt med gyldigTom må angis"));
    }

    @Test
    void whenUgyldigDatoInterval_thenThrowExecption() {

        var request = FullmaktDTO.builder()
                        .omraader(List.of("OMRAADE"))
                        .gyldigFraOgMed(LocalDate.of(2012, 04, 05).atStartOfDay())
                        .gyldigTilOgMed(LocalDate.of(2012, 04, 04).atStartOfDay())
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                fullmaktService.validate(request));

        assertThat(exception.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom"));
    }

    @Test
    void whenStatedPersonDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(false);

        var request = FullmaktDTO.builder()
                        .omraader(List.of("OMRAADE"))
                        .gyldigFraOgMed(LocalDate.of(2012, 04, 05).atStartOfDay())
                        .gyldigTilOgMed(LocalDate.of(2012, 04, 06).atStartOfDay())
                        .motpartsPersonident(IDENT)
                        .isNew(true)
                        .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                fullmaktService.validate(request));

        assertThat(exception.getMessage(), containsString(format("Fullmektig: person %s ikke funnet i database", IDENT)));
    }
}