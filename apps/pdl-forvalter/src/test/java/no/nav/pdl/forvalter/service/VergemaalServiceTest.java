package no.nav.pdl.forvalter.service;

import no.nav.pdl.forvalter.database.repository.PersonRepository;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalEmbete;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VergemaalSakstype;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.verify;
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

        var request = VergemaalDTO.builder()
                .isNew(true)
                .build();

        StepVerifier.create(
                        vergemaalService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Embete for vergemål må angis")));
    }

    @Test
    void whenTypeIsMissing_thenThrowExecption() {

        var request = VergemaalDTO.builder()
                .vergemaalEmbete(VergemaalEmbete.FMAV)
                .isNew(true)
                .build();

        StepVerifier.create(
                        vergemaalService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Sakstype av vergemål må angis")));
    }

    @Test
    void whenUgyldigDatoInterval_thenThrowExecption() {

        var request = VergemaalDTO.builder()
                .vergemaalEmbete(VergemaalEmbete.FMIN)
                .gyldigFraOgMed(LocalDate.of(2012, 4, 5).atStartOfDay())
                .gyldigTilOgMed(LocalDate.of(2012, 4, 4).atStartOfDay())
                .isNew(true)
                .build();

        StepVerifier.create(
                        vergemaalService.validate(request))
                .verifyErrorSatisfies(throwable ->
                        assertThat(throwable.getMessage(), containsString("Ugyldig datointervall: gyldigFom må være før gyldigTom")));
    }

    @Test
    void whenStatedPersonDoesNotExist_thenThrowExecption() {

        when(personRepository.existsByIdent(IDENT)).thenReturn(Mono.just(false));

        var request = VergemaalDTO.builder()
                .vergemaalEmbete(VergemaalEmbete.FMNO)
                .sakType(VergemaalSakstype.EMF)
                .vergeIdent(IDENT)
                .isNew(true)
                .build();

        StepVerifier.create(
                        vergemaalService.validate(request))
                .verifyErrorSatisfies(throwable -> {

                    verify(personRepository).existsByIdent(IDENT);
                    assertThat(throwable.getMessage(), containsString(format("Vergeperson med ident %s ikke funnet i database", IDENT)));
                });
    }
}