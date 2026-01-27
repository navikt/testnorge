package no.nav.skattekortservice.provider;

import no.nav.skattekortservice.service.SkattekortService;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.IdentifikatorForEnhetEllerPerson;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortResponseDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SkattekortControllerTest {

    @Mock
    private SkattekortService skattekortService;

    @InjectMocks
    private SkattekortController skattekortController;

    @Test
    void shouldSendSkattekort() {

        var arbeidsgiverIdent = IdentifikatorForEnhetEllerPerson
                .builder()
                .organisasjonsnummer("999999999")
                .build();
        var skattekortmelding = Skattekortmelding
                .builder()
                .arbeidstakeridentifikator("12345678901")
                .build();
        var arbeidsgiver = ArbeidsgiverSkatt
                .builder()
                .arbeidsgiveridentifikator(arbeidsgiverIdent)
                .arbeidstaker(List.of(skattekortmelding))
                .build();
        var request = SkattekortRequestDTO
                .builder()
                .arbeidsgiver(List.of(arbeidsgiver))
                .build();
        var expectedResponse = "OK";
        when(skattekortService.sendSkattekort(any(SkattekortRequestDTO.class)))
                .thenReturn(Mono.just(expectedResponse));

        var result = skattekortController.sendSkattekort(request);

        StepVerifier
                .create(result)
                .expectNext(expectedResponse)
                .verifyComplete();
        verify(skattekortService).sendSkattekort(request);

    }

    @Test
    void shouldHentSkattekort() {

        var ident = "12345678901";
        var arbeidsgiverIdent = IdentifikatorForEnhetEllerPerson
                .builder()
                .organisasjonsnummer("999999999")
                .build();
        var skattekortmelding = Skattekortmelding
                .builder()
                .arbeidstakeridentifikator(ident)
                .build();
        var arbeidsgiver = ArbeidsgiverSkatt
                .builder()
                .arbeidsgiveridentifikator(arbeidsgiverIdent)
                .arbeidstaker(List.of(skattekortmelding))
                .build();
        var response1 = SkattekortResponseDTO
                .builder()
                .ident(ident)
                .inntektsaar("2025")
                .arbeidsgiver(List.of(arbeidsgiver))
                .build();
        var response2 = SkattekortResponseDTO
                .builder()
                .ident(ident)
                .inntektsaar("2026")
                .arbeidsgiver(List.of(arbeidsgiver))
                .build();
        when(skattekortService.hentSkattekort(eq(ident), eq(null)))
                .thenReturn(Flux.just(response1, response2));

        var result = skattekortController.hentSkattekort(ident, null);
        StepVerifier
                .create(result)
                .expectNext(response1)
                .expectNext(response2)
                .verifyComplete();
        verify(skattekortService).hentSkattekort(ident, null);

    }

    @Test
    void shouldHandleEmptyResponseWhenHentSkattekort() {

        var ident = "12345678901";
        when(skattekortService.hentSkattekort(eq(ident), eq(null)))
                .thenReturn(Flux.empty());

        var result = skattekortController.hentSkattekort(ident, null);
        StepVerifier
                .create(result)
                .verifyComplete();
        verify(skattekortService).hentSkattekort(ident, null);

    }

    @Test
    void shouldHandleErrorWhenSendSkattekort() {

        var request = SkattekortRequestDTO
                .builder()
                .build();
        var expectedError = new RuntimeException("Service error");
        when(skattekortService.sendSkattekort(any(SkattekortRequestDTO.class)))
                .thenReturn(Mono.error(expectedError));

        var result = skattekortController.sendSkattekort(request);
        StepVerifier
                .create(result)
                .expectError(RuntimeException.class)
                .verify();
        verify(skattekortService).sendSkattekort(request);

    }

    @Test
    void shouldHandleErrorWhenHentSkattekort() {

        var ident = "12345678901";
        var expectedError = new RuntimeException("Service error");
        when(skattekortService.hentSkattekort(eq(ident), eq(null)))
                .thenReturn(Flux.error(expectedError));

        var result = skattekortController.hentSkattekort(ident, null);
        StepVerifier
                .create(result)
                .expectError(RuntimeException.class)
                .verify();
        verify(skattekortService).hentSkattekort(ident, null);

    }

}

