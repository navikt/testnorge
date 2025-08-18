package no.nav.dolly.bestilling.inntektsmelding;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.inntektsmelding.domain.InntektsmeldingResponse;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;
import no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests.InntektsmeldingRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InntektsmeldingClientTest {

    @Mock
    private InntektsmeldingConsumer inntektsmeldingConsumer;

    @Mock
    private TransaksjonMappingService transaksjonMappingService;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private ApplicationConfig applicationConfig;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private InntektsmeldingClient inntektsmeldingClient;

    @Test
    void shouldCallTransaksjonMappingServiceForEachEnvironment() throws Exception {

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
                .thenReturn(Mono.just(inntektsmeldingResponse));
        when(transactionHelperService.persister(any(), any(), any(), any())).thenReturn(Mono.just(new BestillingProgress()));
        when(applicationConfig.getClientTimeout()).thenReturn(1000L);
        when(transaksjonMappingService.saveAll(any())).thenReturn(Mono.empty());
        when(mapperFacade.map(any(), eq(InntektsmeldingRequest.class), any())).thenReturn(InntektsmeldingRequest.builder()
                .inntekter(List.of(new RsInntektsmeldingRequest()))
                .build());
        when(objectMapper.writeValueAsString(any())).thenReturn("{\"key\":\"value\"}");

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

        ArgumentCaptor<List<TransaksjonMapping>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        StepVerifier
                .create(inntektsmeldingClient.gjenopprett(bestilling, person, progress, true))
                .assertNext(status -> {
                    verify(transaksjonMappingService, times(2))
                            .saveAll(argumentCaptor.capture());
                    assertThat(argumentCaptor.getAllValues(), hasSize(2));
                    assertThat(argumentCaptor.getAllValues().getFirst().getFirst().getMiljoe(), is(oneOf("q1", "q2")));
                    assertThat(argumentCaptor.getAllValues().getLast().getFirst().getMiljoe(), is(oneOf("q1", "q2")));
                })
                .verifyComplete();
    }
}