package no.nav.dolly.bestilling.oppfoelgingsvedtak14a;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.Oppfoelgingsvedtak14aRequestDTO;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.ResponseStatusDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.oppfoelgingsvedtak14a.RsOppfoelgingsvedtak14aDTO;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
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

import static no.nav.dolly.domain.resultset.SystemTyper.OPPFOELGINGSVEDTAK14A;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Oppfoelgingsvedtak14aClientTest {

    private static final String IDENT = "12345678901";
    private static final String NORG_ENHET = "0315";
    private static final String KOMMUNE_ENHET = "1554";

    @Mock
    private Oppfoelgingsvedtak14aConsumer oppfoelgingsvedtak14aConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private Norg2Consumer norg2Consumer;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @InjectMocks
    private Oppfoelgingsvedtak14aClient oppfoelgingsvedtak14aClient;

    @Test
    void shouldReturnEmpty_whenBistandsbehovIsNull() {
        StepVerifier.create(oppfoelgingsvedtak14aClient.gjenopprett(
                        new RsDollyUtvidetBestilling(),
                        DollyPerson.builder().ident(IDENT).build(),
                        new BestillingProgress(),
                        false))
                .verifyComplete();

        verify(oppfoelgingsvedtak14aConsumer, never()).startOppfoelgingsperiode(anyString());
    }

    @Test
    void shouldGjenopprett_OK_withExplicitOppfolgingsEnhet() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(oppfoelgingsvedtak14aConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(mapperFacade.map(any(), eq(Oppfoelgingsvedtak14aRequestDTO.class), any()))
                .thenReturn(new Oppfoelgingsvedtak14aRequestDTO());
        when(mapperFacade.map(any(Oppfoelgingsvedtak14aRequestDTO.class), eq(RsOppfoelgingsvedtak14aDTO.class)))
                .thenReturn(new RsOppfoelgingsvedtak14aDTO());
        when(oppfoelgingsvedtak14aConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(oppfoelgingsvedtak14aClient.gjenopprett(
                        bestillingMed(NORG_ENHET),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), statusCaptor.capture());
                    verify(norg2Consumer, never()).getNorgEnhet(anyString());
                    assertThat(statusCaptor.getAllValues().getFirst())
                            .isEqualTo(getInfoVenter(OPPFOELGINGSVEDTAK14A.getBeskrivelse()));
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo("OK");
                })
                .verifyComplete();
    }

    @Test
    void shouldGjenopprett_OK_withNorgEnhetFromKommune() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(oppfoelgingsvedtak14aConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(personServiceConsumer.getPdlPersoner(anyList()))
                .thenReturn(Flux.just(pdlPersonBolkMedKommune()));
        when(norg2Consumer.getNorgEnhet(KOMMUNE_ENHET))
                .thenReturn(Mono.just(Norg2EnhetResponse.builder().enhetNr(NORG_ENHET).build()));
        when(mapperFacade.map(any(), eq(Oppfoelgingsvedtak14aRequestDTO.class), any()))
                .thenReturn(new Oppfoelgingsvedtak14aRequestDTO());
        when(mapperFacade.map(any(Oppfoelgingsvedtak14aRequestDTO.class), eq(RsOppfoelgingsvedtak14aDTO.class)))
                .thenReturn(new RsOppfoelgingsvedtak14aDTO());
        when(oppfoelgingsvedtak14aConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(oppfoelgingsvedtak14aClient.gjenopprett(
                        bestillingMed(null),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), statusCaptor.capture());
                    verify(norg2Consumer).getNorgEnhet(KOMMUNE_ENHET);
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo("OK");
                })
                .verifyComplete();
    }

    @Test
    void shouldGjenopprett_OK_withFallbackEnhet_whenNorg2ReturnsError() {
        val progress = new BestillingProgress();

        stubPersister(progress);
        when(oppfoelgingsvedtak14aConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(personServiceConsumer.getPdlPersoner(anyList()))
                .thenReturn(Flux.just(pdlPersonBolkMedKommune()));
        when(norg2Consumer.getNorgEnhet(KOMMUNE_ENHET))
                .thenReturn(Mono.just(Norg2EnhetResponse.builder().httpStatus(HttpStatus.BAD_GATEWAY).build()));
        when(mapperFacade.map(any(), eq(Oppfoelgingsvedtak14aRequestDTO.class), any()))
                .thenReturn(new Oppfoelgingsvedtak14aRequestDTO());
        when(mapperFacade.map(any(Oppfoelgingsvedtak14aRequestDTO.class), eq(RsOppfoelgingsvedtak14aDTO.class)))
                .thenReturn(new RsOppfoelgingsvedtak14aDTO());
        when(oppfoelgingsvedtak14aConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(oppfoelgingsvedtak14aClient.gjenopprett(
                        bestillingMed(null),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> verify(oppfoelgingsvedtak14aConsumer).opprettBistandVedtak(any()))
                .verifyComplete();
    }

    @Test
    void shouldGjenopprett_Feil_whenStartOppfoelgingsperiodeFails() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(oppfoelgingsvedtak14aConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason("Service unavailable")
                        .build()));

        StepVerifier.create(oppfoelgingsvedtak14aClient.gjenopprett(
                        bestillingMed(NORG_ENHET),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), statusCaptor.capture());
                    verify(oppfoelgingsvedtak14aConsumer, never()).opprettBistandVedtak(any());
                    assertThat(statusCaptor.getAllValues().getLast())
                            .startsWith("Feil= Start oppfølgingsperiode feilet");
                })
                .verifyComplete();
    }

    @Test
    void shouldGjenopprett_Feil_whenOpprettBistandVedtakFails() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(oppfoelgingsvedtak14aConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(mapperFacade.map(any(), eq(Oppfoelgingsvedtak14aRequestDTO.class), any()))
                .thenReturn(new Oppfoelgingsvedtak14aRequestDTO());
        when(mapperFacade.map(any(Oppfoelgingsvedtak14aRequestDTO.class), eq(RsOppfoelgingsvedtak14aDTO.class)))
                .thenReturn(new RsOppfoelgingsvedtak14aDTO());
        when(oppfoelgingsvedtak14aConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .reason("Vedtak finnes allerede")
                        .build()));

        StepVerifier.create(oppfoelgingsvedtak14aClient.gjenopprett(
                        bestillingMed(NORG_ENHET),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), statusCaptor.capture());
                    assertThat(statusCaptor.getAllValues().getLast())
                            .startsWith("Feil= Opprett bistandsvedtak feilet");
                })
                .verifyComplete();
    }

    private void stubPersister(BestillingProgress progress) {
        when(transactionHelperService.persister(any(), any(), any(), anyString()))
                .thenReturn(Mono.just(progress));
        lenient().when(transactionHelperService.persister(any(), any(RsDollyBestilling.class)))
                .thenReturn(Mono.just(new Bestilling()));
    }

    private static RsDollyUtvidetBestilling bestillingMed(String oppfolgingsEnhet) {
        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setOppfoelgingsvedtak14a(RsOppfoelgingsvedtak14aDTO.builder()
                .oppfolgingsEnhet(oppfolgingsEnhet)
                .build());
        return bestilling;
    }

    private static PdlPersonBolk pdlPersonBolkMedKommune() {
        return PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentGeografiskTilknytningBolk(List.of(
                                PdlPersonBolk.GeografiskTilknytningBolk.builder()
                                        .geografiskTilknytning(PdlPersonBolk.GeografiskTilknytning.builder()
                                                .gtKommune(KOMMUNE_ENHET)
                                                .build())
                                        .build()))
                        .build())
                .build();
    }
}
