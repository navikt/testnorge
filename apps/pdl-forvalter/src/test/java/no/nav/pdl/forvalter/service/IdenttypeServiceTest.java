package no.nav.pdl.forvalter.service;

import no.nav.testnav.libs.dto.pdlforvalter.v1.IdentRequestDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.Identtype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@ExtendWith(MockitoExtension.class)
class IdenttypeServiceTest {

    @InjectMocks
    private IdenttypeService identtypeService;

    @Test
    void whenFutureDatoIsProvided_thenThrowError() {

        var request = IdentRequestDTO.builder()
                .foedtEtter(LocalDateTime.now().plusDays(1))
                .isNew(true)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Identtype ugyldig forespørsel: støttet datointervall " +
                                "er fødsel mellom 1.1.1900 og dagens dato")));
    }

    @Test
    void whenPre1900DatoIsProvided_thenThrowError() {

        var request = IdentRequestDTO.builder()
                .foedtFoer(LocalDateTime.of(1870, 1, 1, 0, 0))
                .isNew(true)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Identtype ugyldig forespørsel: støttet datointervall " +
                                "er fødsel mellom 1.1.1900 og dagens dato")));
    }

    @Test
    void whenIllegalDatoIntervalIsProvided_thenThrowError() {

        var request = IdentRequestDTO.builder()
                .identtype(Identtype.FNR)
                .foedtEtter(LocalDateTime.of(1980, 1, 1, 1, 0, 0))
                .foedtFoer(LocalDateTime.of(1970, 1, 1, 0, 0))
                .isNew(true)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Identtype ugyldig forespørsel: fødtFør kan ikke være " +
                                "tidligere enn fødtEtter")));
    }

    @Test
    void whenAlderIsOutsideBoundaries_thenThrowError() {

        var request = IdentRequestDTO.builder()
                .alder(-1)
                .isNew(true)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Alder må være mellom 0 og 120 år")));
    }

    @Test
    void whenAlderIsOutsideBoundaries2_thenThrowError() {

        var request = IdentRequestDTO.builder()
                .alder(150)
                .isNew(true)
                .build();

        StepVerifier.create(identtypeService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Alder må være mellom 0 og 120 år")));
    }
}