package no.nav.dolly.bestilling.inntektsmelding;

import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.service.TransaksjonMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class InntektsmeldingClientTest {

    @Autowired
    private InntektsmeldingClient inntektsmeldingClient;

    @MockitoBean
    private InntektsmeldingConsumer inntektsmeldingConsumer;

    @MockitoBean
    private TransaksjonMappingService transaksjonMappingService;

    @Test
    void shouldCallTransaksjonMappingServiceTwiceForEachEnvironment() {

        doCallRealMethod()
                .when(transaksjonMappingService).saveAll(any());

        var dokument = InntektsmeldingResponse.Dokument
                .builder()
                .dokumentInfoId("dokumentInfoId")
                .journalpostId("journalpostId")
                .build();
        var inntektsmeldingResponse = InntektsmeldingResponse
                .builder()
                .dokumenter(List.of(dokument))
                .build();
        when(inntektsmeldingConsumer.postInntektsmelding(any()))
                .thenReturn(Flux.just(inntektsmeldingResponse));

        var inntekter = RsInntektsmelding.Inntektsmelding
                .builder()
                .build();
        var inntektsmelding = RsInntektsmelding
                .builder()
                .inntekter(List.of(inntekter))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInntektsmelding(inntektsmelding);
        bestilling.setEnvironments(Set.of("q1", "q2"));

        var person = DollyPerson
                .builder()
                .ident("12345678901")
                .build();
        var progress = BestillingProgress.builder().build();

        StepVerifier
                .create(inntektsmeldingClient.gjenopprett(bestilling, person, progress, true))
                .expectNextCount(1)
                .expectComplete()
                .verify();

        verify(transaksjonMappingService, times(1))
                .save(argThat(transaksjonMapping -> transaksjonMapping.getMiljoe().equals("q1")));
        verify(transaksjonMappingService, times(1))
                .save(argThat(transaksjonMapping -> transaksjonMapping.getMiljoe().equals("q2")));


    }

}
