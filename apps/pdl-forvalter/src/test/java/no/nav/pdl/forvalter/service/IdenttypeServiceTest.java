package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.domain.IdentRequestDTO;
import no.nav.pdl.forvalter.domain.Identtype;
import no.nav.pdl.forvalter.domain.PersonDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class IdenttypeServiceTest {

    @InjectMocks
    private IdenttypeService identtypeService;

    @Test
    void whenIllegalIdenttype_thenThrowError() {

        var request = PersonDTO.builder()
                .nyident(List.of(IdentRequestDTO.builder()
                        .identtype(Identtype.FDAT)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                identtypeService.convert(request));

        assertThat(exception.getMessage(), containsString("Identtype må være en av FNR, DNR eller BOST"));
    }

    @Test
    void whenFutureDatoIsProvided_thenThrowError() {

        var request = PersonDTO.builder()
                .nyident(List.of(IdentRequestDTO.builder()
                        .foedtEtter(LocalDateTime.now().plusDays(1))
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                identtypeService.convert(request));

        assertThat(exception.getMessage(), containsString("Identtype ugyldig forespørsel: støttet datointervall " +
                "er fødsel mellom 1.1.1900 og dagens dato"));
    }

    @Test
    void whenPre1900DatoIsProvided_thenThrowError() {

        var request = PersonDTO.builder()
                .nyident(List.of(IdentRequestDTO.builder()
                        .foedtFoer(LocalDateTime.of(1870, 1, 1, 0, 0))
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                identtypeService.convert(request));

        assertThat(exception.getMessage(), containsString("Identtype ugyldig forespørsel: støttet datointervall " +
                "er fødsel mellom 1.1.1900 og dagens dato"));
    }

    @Test
    void whenIllegalDatoIntervalIsProvided_thenThrowError() {

        var request = PersonDTO.builder()
                .nyident(List.of(IdentRequestDTO.builder()
                        .identtype(Identtype.FNR)
                        .foedtEtter(LocalDateTime.of(1980, 1, 1, 1, 0, 0))
                        .foedtFoer(LocalDateTime.of(1970, 1, 1, 0, 0))
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                identtypeService.convert(request));

        assertThat(exception.getMessage(), containsString("Identtype ugyldig forespørsel: fødtFør kan ikke være " +
                "tidligere enn fødtEtter"));
    }

    @Test
    void whenAlderIsOutsideBoundaries_thenThrowError() {

        var request = PersonDTO.builder()
                .nyident(List.of(IdentRequestDTO.builder()
                        .alder(-1)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                identtypeService.convert(request));

        assertThat(exception.getMessage(), containsString("Alder må være mellom 0 og 120 år"));
    }

    @Test
    void whenAlderIsOutsideBoundaries2_thenThrowError() {

        var request = PersonDTO.builder()
                .nyident(List.of(IdentRequestDTO.builder()
                        .alder(-1)
                        .isNew(true)
                        .build()))
                .build();

        var exception = assertThrows(HttpClientErrorException.class, () ->
                identtypeService.convert(request));

        assertThat(exception.getMessage(), containsString("Alder må være mellom 0 og 120 år"));
    }
}