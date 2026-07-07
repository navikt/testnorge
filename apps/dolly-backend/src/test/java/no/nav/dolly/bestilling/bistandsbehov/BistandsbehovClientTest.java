package no.nav.dolly.bestilling.bistandsbehov;

import lombok.val;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.bistandsbehov.dto.BistandVedtakRequestDTO;
import no.nav.dolly.bestilling.bistandsbehov.dto.ResponseStatusDTO;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.bistandsbehov.RsBistandsbehovDTO;
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

import static no.nav.dolly.domain.resultset.SystemTyper.BISTANDSBEHOV;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BistandsbehovClientTest {

    private static final String IDENT = "12345678901";
    private static final String NORG_ENHET = "0315";
    private static final String KOMMUNE_ENHET = "1554";

    @Mock
    private BistandsbehovConsumer bistandsbehovConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private Norg2Consumer norg2Consumer;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @InjectMocks
    private BistandsbehovClient bistandsbehovClient;

    @Test
    void shouldReturnEmpty_whenBistandsbehovIsNull() {
        StepVerifier.create(bistandsbehovClient.gjenopprett(
                        new RsDollyUtvidetBestilling(),
                        DollyPerson.builder().ident(IDENT).build(),
                        new BestillingProgress(),
                        false))
                .verifyComplete();

        verify(bistandsbehovConsumer, never()).startOppfoelgingsperiode(anyString());
    }

    @Test
    void shouldGjenopprett_OK_withExplicitOppfolgingsEnhet() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(bistandsbehovConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(mapperFacade.map(any(), eq(BistandVedtakRequestDTO.class), any()))
                .thenReturn(new BistandVedtakRequestDTO());
        when(bistandsbehovConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(bistandsbehovClient.gjenopprett(
                        bestillingMed(NORG_ENHET),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), statusCaptor.capture());
                    verify(norg2Consumer, never()).getNorgEnhet(anyString());
                    assertThat(statusCaptor.getAllValues().getFirst())
                            .isEqualTo(getInfoVenter(BISTANDSBEHOV.getBeskrivelse()));
                    assertThat(statusCaptor.getAllValues().getLast()).isEqualTo("OK");
                })
                .verifyComplete();
    }

    @Test
    void shouldGjenopprett_OK_withNorgEnhetFromKommune() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(bistandsbehovConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(personServiceConsumer.getPdlPersoner(anyList()))
                .thenReturn(Flux.just(pdlPersonBolkMedKommune(KOMMUNE_ENHET)));
        when(norg2Consumer.getNorgEnhet(KOMMUNE_ENHET))
                .thenReturn(Mono.just(Norg2EnhetResponse.builder().enhetNr(NORG_ENHET).build()));
        when(mapperFacade.map(any(), eq(BistandVedtakRequestDTO.class), any()))
                .thenReturn(new BistandVedtakRequestDTO());
        when(bistandsbehovConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(bistandsbehovClient.gjenopprett(
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
        when(bistandsbehovConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(personServiceConsumer.getPdlPersoner(anyList()))
                .thenReturn(Flux.just(pdlPersonBolkMedKommune(KOMMUNE_ENHET)));
        when(norg2Consumer.getNorgEnhet(KOMMUNE_ENHET))
                .thenReturn(Mono.just(Norg2EnhetResponse.builder().httpStatus(HttpStatus.BAD_GATEWAY).build()));
        when(mapperFacade.map(any(), eq(BistandVedtakRequestDTO.class), any()))
                .thenReturn(new BistandVedtakRequestDTO());
        when(bistandsbehovConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));

        StepVerifier.create(bistandsbehovClient.gjenopprett(
                        bestillingMed(null),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> verify(bistandsbehovConsumer).opprettBistandVedtak(any()))
                .verifyComplete();
    }

    @Test
    void shouldGjenopprett_Feil_whenStartOppfoelgingsperiodeFails() {
        val progress = new BestillingProgress();
        val statusCaptor = ArgumentCaptor.forClass(String.class);

        stubPersister(progress);
        when(bistandsbehovConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .reason("Service unavailable")
                        .build()));

        StepVerifier.create(bistandsbehovClient.gjenopprett(
                        bestillingMed(NORG_ENHET),
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        false))
                .assertNext(_ -> {
                    verify(transactionHelperService, times(2)).persister(any(), any(), any(), statusCaptor.capture());
                    verify(bistandsbehovConsumer, never()).opprettBistandVedtak(any());
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
        when(bistandsbehovConsumer.startOppfoelgingsperiode(IDENT))
                .thenReturn(Mono.just(ResponseStatusDTO.builder().status(HttpStatus.OK).build()));
        when(mapperFacade.map(any(), eq(BistandVedtakRequestDTO.class), any()))
                .thenReturn(new BistandVedtakRequestDTO());
        when(bistandsbehovConsumer.opprettBistandVedtak(any()))
                .thenReturn(Mono.just(ResponseStatusDTO.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .reason("Vedtak finnes allerede")
                        .build()));

        StepVerifier.create(bistandsbehovClient.gjenopprett(
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
    }

    private static RsDollyUtvidetBestilling bestillingMed(String oppfolgingsEnhet) {
        val bestilling = new RsDollyUtvidetBestilling();
        bestilling.setBistandsbehov(RsBistandsbehovDTO.builder()
                .oppfolgingsEnhet(oppfolgingsEnhet)
                .build());
        return bestilling;
    }

    private static PdlPersonBolk pdlPersonBolkMedKommune(String gtKommune) {
        return PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentGeografiskTilknytningBolk(List.of(
                                PdlPersonBolk.GeografiskTilknytningBolk.builder()
                                        .geografiskTilknytning(PdlPersonBolk.GeografiskTilknytning.builder()
                                                .gtKommune(gtKommune)
                                                .build())
                                        .build()))
                        .build())
                .build();
    }
}
