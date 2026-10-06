package no.nav.dolly.bestilling.inntektstub;

import lombok.val;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.ResponseDTO;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntekter;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InntektstubClientTest {

    private static final String TESTNORGE_IDENT = "11811111111";
    private static final String DOLLY_IDENT = "11411111111";

    @Mock
    private InntektstubConsumer inntektstubConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private InntektstubClient inntektstubClient;

    @Test
    void shouldImportFraTenor_OK() {

        val dollyPerson = DollyPerson.builder().ident(TESTNORGE_IDENT).build();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(inntektstubConsumer.sjekkImporterInntekt(eq(TESTNORGE_IDENT), anyBoolean()))
                .thenReturn(Mono.just(ResponseDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(inntektstubClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(true));
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(false));
                    assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo("Info= Oppretting startet mot Inntektstub (INNTK) ...");
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo("OK");
                })
                .verifyComplete();
    }

    @Test
    void shouldImportFraTenor_Error() {

        val dollyPerson = DollyPerson.builder().ident(TESTNORGE_IDENT).build();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(inntektstubConsumer.sjekkImporterInntekt(eq(TESTNORGE_IDENT), anyBoolean()))
                .thenReturn(Mono.just(ResponseDTO.builder().status(HttpStatus.OK).build()))
                .thenReturn(Mono.just(ResponseDTO.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("Blah").build()));

        StepVerifier.create(inntektstubClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(false));
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(true));
                    assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo("Info= Oppretting startet mot Inntektstub (INNTK) ...");
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo("Feil= Import av inntektsdata feilet= Blah");
                })
                .verifyComplete();
    }

    @Test
    void shouldIkkeFunnetFraTenorSjekk_OK() {

        val dollyPerson = DollyPerson.builder().ident(TESTNORGE_IDENT).build();

        when(inntektstubConsumer.sjekkImporterInntekt(eq(TESTNORGE_IDENT), anyBoolean()))
                .thenReturn(Mono.just(ResponseDTO.builder().status(HttpStatus.NOT_FOUND).build()));

        StepVerifier.create(inntektstubClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyPerson, new BestillingProgress(), true))
                .expectNextCount(0)
                .verifyComplete();

        verify(inntektstubConsumer).sjekkImporterInntekt(TESTNORGE_IDENT, true);
    }

    @Test
    void shouldSendInntektsdataToInntektstub_OK() {

        val statusCaptor = ArgumentCaptor.forClass(String.class);
        val dollyPerson = DollyPerson.builder().ident(DOLLY_IDENT).build();
        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInntektstub(buildInntektsinformasjon());

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        stubInntektsinformasjonMapping();
        when(inntektstubConsumer.getInntekter(anyString())).thenReturn(Flux.empty());
        val postedInntekter = new ArrayList<List<Inntektsinformasjon>>();
        when(inntektstubConsumer.postInntekter(any()))
                .thenAnswer(invocation -> {
                    postedInntekter.add(invocation.getArgument(0));
                    return Flux.just(Inntektsinformasjon.builder().build());
                });

        StepVerifier.create(inntektstubClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(inntektstubConsumer).getInntekter(anyString());
                    verify(inntektstubConsumer).postInntekter(any());
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo("Info= Oppretting startet mot Inntektstub (INNTK) ...");
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo(",OK");
                })
                .verifyComplete();

        assertThat(postedInntekter.getFirst())
                .extracting(Inntektsinformasjon::getAarMaaned)
                .containsExactly("2025-12", "2025-11", "2025-10", "2025-09");
        assertThat(postedInntekter.getFirst())
                .extracting(Inntektsinformasjon::getNorskIdent)
                .containsOnly(DOLLY_IDENT);
    }

    @Test
    void shouldSendInntektsdataToInntektstub_FeilVedLagring() {

        val statusCaptor = ArgumentCaptor.forClass(String.class);
        val dollyPerson = DollyPerson.builder().ident(DOLLY_IDENT).build();
        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInntektstub(buildInntektsinformasjon());

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        stubInntektsinformasjonMapping();
        when(inntektstubConsumer.getInntekter(anyString())).thenReturn(Flux.empty());
        when(inntektstubConsumer.postInntekter(any()))
                .thenReturn(Flux.just(Inntektsinformasjon.builder()
                        .feilmelding("Feil ved lagring")
                        .build()));

        StepVerifier.create(inntektstubClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(inntektstubConsumer).getInntekter(anyString());
                    verify(inntektstubConsumer).postInntekter(any());
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo("Info= Oppretting startet mot Inntektstub (INNTK) ...");
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo(",Feil= Feil ved lagring");
                })
                .verifyComplete();
    }

    @Test
    void shouldSendInntekterDataToInntektstub() {

        val statusCaptor = ArgumentCaptor.forClass(String.class);
        val dollyPerson = DollyPerson.builder().ident(DOLLY_IDENT).build();
        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInntekter(List.of(RsInntekter.builder()
                .perioder(List.of(YearMonth.of(2025, 12), YearMonth.of(2025, 11)))
                .build()));

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(mapperFacade.map(any(RsInntekter.class), eq(Inntektsinformasjon.class), any()))
                .thenAnswer(invocation -> mapInntektsinformasjon(invocation.getArgument(2)));
        when(inntektstubConsumer.getInntekter(DOLLY_IDENT)).thenReturn(Flux.empty());
        val postedInntekter = new ArrayList<List<Inntektsinformasjon>>();
        when(inntektstubConsumer.postInntekter(any()))
                .thenAnswer(invocation -> {
                    postedInntekter.add(invocation.getArgument(0));
                    return Flux.just(Inntektsinformasjon.builder().build());
                });

        StepVerifier.create(inntektstubClient.gjenopprett(
                        bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(_ -> {
                    verify(inntektstubConsumer).getInntekter(DOLLY_IDENT);
                    verify(inntektstubConsumer).postInntekter(any());
                    verify(transactionHelperService).persister(any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getValue()).isEqualTo(",OK");
                })
                .verifyComplete();

        assertThat(postedInntekter.getFirst())
                .extracting(Inntektsinformasjon::getAarMaaned)
                .containsExactly("2025-12", "2025-11");
        assertThat(postedInntekter.getFirst())
                .extracting(Inntektsinformasjon::getNorskIdent)
                .containsOnly(DOLLY_IDENT);
    }

    @Test
    void shouldFailGjenopprettWhenGetInntekterThrowsInternalServerError() {

        val dollyPerson = DollyPerson.builder().ident(DOLLY_IDENT).build();
        val bestilling = new RsDollyUtvidetBestilling();
        val internalServerError = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                null,
                null,
                null);
        bestilling.setInntektstub(buildInntektsinformasjon());

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(inntektstubConsumer.getInntekter(DOLLY_IDENT))
                .thenReturn(Flux.error(internalServerError));

        StepVerifier.create(inntektstubClient.gjenopprett(
                        bestilling, dollyPerson, new BestillingProgress(), true))
                .expectErrorSatisfies(error -> {
                    org.assertj.core.api.Assertions.assertThat(error).isSameAs(internalServerError);
                    org.assertj.core.api.Assertions.assertThat(
                                    ((WebClientResponseException) error).getStatusCode())
                            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                })
                .verify();

        verify(inntektstubConsumer).getInntekter(DOLLY_IDENT);
        verify(inntektstubConsumer, never()).postInntekter(any());
        verify(transactionHelperService).persister(any(), any(),
                eq("Info= Oppretting startet mot Inntektstub (INNTK) ..."));
    }

    private void stubInntektsinformasjonMapping() {

        when(mapperFacade.map(any(RsInntektsinformasjon.class), eq(Inntektsinformasjon.class), any()))
                .thenAnswer(invocation -> mapInntektsinformasjon(invocation.getArgument(2)));
    }

    private static Inntektsinformasjon mapInntektsinformasjon(MappingContext mappingContext) {

        return Inntektsinformasjon.builder()
                .norskIdent((String) mappingContext.getProperty("ident"))
                .aarMaaned(((YearMonth) mappingContext.getProperty("periode")).toString())
                .build();
    }

    private static InntektMultiplierWrapper buildInntektsinformasjon() {

        return InntektMultiplierWrapper.builder()
                .inntektsinformasjon(List.of(RsInntektsinformasjon.builder()
                        .antallMaaneder(4)
                        .sisteAarMaaned("2025-12")
                        .build()))
                .build();
    }
}