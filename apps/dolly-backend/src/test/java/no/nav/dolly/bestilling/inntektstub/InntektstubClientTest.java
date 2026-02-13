package no.nav.dolly.bestilling.inntektstub;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.inntektstub.domain.CheckImportResponse;
import no.nav.dolly.bestilling.inntektstub.domain.Inntektsinformasjon;
import no.nav.dolly.bestilling.inntektstub.domain.InntektsinformasjonWrapper;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
                .thenReturn(Mono.just(CheckImportResponse.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(inntektstubClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyPerson, new BestillingProgress(), true))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(true));
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(false));
                    assertThat(statusCaptor.getAllValues().getFirst(), equalTo("Info= Oppretting startet mot Inntektstub (INNTK) ..."));
                    assertThat(statusCaptor.getAllValues().getLast(), equalTo("OK"));
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
                .thenReturn(Mono.just(CheckImportResponse.builder().status(HttpStatus.OK).build()))
                .thenReturn(Mono.just(CheckImportResponse.builder().status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .message("Blah").build()));

        StepVerifier.create(inntektstubClient.gjenopprett(new RsDollyUtvidetBestilling(), dollyPerson, new BestillingProgress(), true))
                .assertNext(status -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(false));
                    verify(inntektstubConsumer).sjekkImporterInntekt(eq(TESTNORGE_IDENT), eq(true));
                    assertThat(statusCaptor.getAllValues().getFirst(), equalTo("Info= Oppretting startet mot Inntektstub (INNTK) ..."));
                    assertThat(statusCaptor.getAllValues().getLast(), equalTo("Feil= Import av inntektsdata feilet= Blah"));
                })
                .verifyComplete();
    }

    @Test
    void shouldIkkeFunnetFraTenorSjekk_OK() {

        val dollyPerson = DollyPerson.builder().ident(TESTNORGE_IDENT).build();

        when(inntektstubConsumer.sjekkImporterInntekt(eq(TESTNORGE_IDENT), anyBoolean()))
                .thenReturn(Mono.just(CheckImportResponse.builder().status(HttpStatus.NOT_FOUND).build()));

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
        when(inntektstubConsumer.getInntekter(anyString()))
                .thenReturn(Flux.just(Inntektsinformasjon.builder().build()));
        when(mapperFacade.map(any(InntektMultiplierWrapper.class), eq(InntektsinformasjonWrapper.class), any()))
                .thenReturn(new InntektsinformasjonWrapper());
        when(inntektstubConsumer.postInntekter(any()))
                .thenReturn(Flux.just(Inntektsinformasjon.builder()
                        .build()));

        StepVerifier.create(inntektstubClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(status -> {
                    verify(inntektstubConsumer).getInntekter(anyString());
                    verify(inntektstubConsumer).postInntekter(any());
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getFirst(), equalTo("Info= Oppretting startet mot Inntektstub (INNTK) ..."));
                    assertThat(statusCaptor.getAllValues().getLast(), equalTo("OK"));
                })
                .verifyComplete();
    }

    @Test
    void shouldSendInntektsdataToInntektstub_FeilVedLagring() {

        val statusCaptor = ArgumentCaptor.forClass(String.class);
        val dollyPerson = DollyPerson.builder().ident(DOLLY_IDENT).build();
        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInntektstub(buildInntektsinformasjon());

        when(transactionHelperService.persister(any(), any(), anyString()))
                .thenReturn(Mono.just(new BestillingProgress()));
        when(inntektstubConsumer.getInntekter(anyString()))
                .thenReturn(Flux.just(Inntektsinformasjon.builder().build()));
        when(mapperFacade.map(any(InntektMultiplierWrapper.class), eq(InntektsinformasjonWrapper.class), any()))
                .thenReturn(new InntektsinformasjonWrapper());
        when(inntektstubConsumer.postInntekter(any()))
                .thenReturn(Flux.just(Inntektsinformasjon.builder()
                        .feilmelding("Feil ved lagring")
                        .build()));

        StepVerifier.create(inntektstubClient.gjenopprett(bestilling, dollyPerson, new BestillingProgress(), true))
                .assertNext(status -> {
                    verify(inntektstubConsumer).getInntekter(anyString());
                    verify(inntektstubConsumer).postInntekter(any());
                    verify(transactionHelperService, times(2)).persister(any(), any(),
                            statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getFirst(), equalTo("Info= Oppretting startet mot Inntektstub (INNTK) ..."));
                    assertThat(statusCaptor.getAllValues().getLast(), equalTo("Feil= Feil ved lagring"));
                })
                .verifyComplete();
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