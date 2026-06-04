package no.nav.dolly.bestilling.instdata.service;

import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.inst.RsInstdataKdi;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstKdiHendelseServiceTest {

    private static final Long BESTILLING_ID = 42L;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private InstKdiHendelseService instKdiHendelseService;

    @Test
    void shouldReturnBestillingUnchangedWhenNotOpprettEndre() {

        var kdi = RsInstdataKdi.builder().build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, false))
                .assertNext(result -> assertThat(result, is(kdi)))
                .verifyComplete();

        verify(transactionHelperService, never()).persister(any(Long.class), any(RsDollyUtvidetBestilling.class));
    }

    @Test
    void shouldPersistAndReturnWhenOpprettEndre() {

        var kdi = RsInstdataKdi.builder().build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> assertThat(result, is(kdi)))
                .verifyComplete();

        verify(transactionHelperService).persister((BESTILLING_ID), bestilling);
    }

    @Test
    void shouldSetMeldingIdWhenBlank() {

        var hendelse = RsInstdataKdi.Innsettelse.builder().build();
        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> assertThat(result.getInnsettelse().getFirst().getMeldingId(), is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void shouldPreserveExistingMeldingId() {

        var hendelse = RsInstdataKdi.Innsettelse.builder()
                .meldingId("existing-id")
                .build();
        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result ->
                        assertThat(result.getInnsettelse().getFirst().getMeldingId(), is(equalTo("existing-id"))))
                .verifyComplete();
    }

    @Test
    void shouldSetHendelseIdWhenBlank() {

        var hendelse = RsInstdataKdi.Innsettelse.builder().build();
        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> {
                    var hendelseId = result.getInnsettelse().getFirst().getHendelseId();
                    assertThat(hendelseId, is(notNullValue()));
                    assertThat(hendelseId, startsWith("0x"));
                })
                .verifyComplete();
    }

    @Test
    void shouldPreserveExistingHendelseId() {

        var hendelse = RsInstdataKdi.Innsettelse.builder()
                .hendelseId("existing-hendelse-id")
                .build();
        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result ->
                        assertThat(result.getInnsettelse().getFirst().getHendelseId(), is(equalTo("existing-hendelse-id"))))
                .verifyComplete();
    }

    @Test
    void shouldSetPubliseringstidspunktWhenNull() {

        var hendelse = RsInstdataKdi.Innsettelse.builder().build();
        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result ->
                        assertThat(result.getInnsettelse().getFirst().getPubliseringstidspunkt(), is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void shouldPreserveExistingPubliseringstidspunkt() {

        var existing = LocalDateTime.of(2025, 1, 15, 10, 0);
        var hendelse = RsInstdataKdi.Innsettelse.builder()
                .publiseringstidspunkt(existing)
                .build();
        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result ->
                        assertThat(result.getInnsettelse().getFirst().getPubliseringstidspunkt(), is(equalTo(existing))))
                .verifyComplete();
    }

    @Test
    void shouldProcessAllHendelseTypes() {

        var innsettelse = RsInstdataKdi.Innsettelse.builder().build();
        var avbruddStart = RsInstdataKdi.AvbruddStart.builder().build();
        var avbruddSlutt = RsInstdataKdi.AvbruddSlutt.builder().build();
        var loeslatelse = RsInstdataKdi.Loeslatelse.builder().build();
        var forventet = RsInstdataKdi.ForventetLoeslatelse.builder().build();

        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(innsettelse))
                .avbruddStart(List.of(avbruddStart))
                .avbruddSlutt(List.of(avbruddSlutt))
                .loeslatelse(List.of(loeslatelse))
                .forventetLoeslatelse(List.of(forventet))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> {
                    assertThat(result.getInnsettelse().getFirst().getHendelseId(), is(notNullValue()));
                    assertThat(result.getAvbruddStart().getFirst().getHendelseId(), is(notNullValue()));
                    assertThat(result.getAvbruddSlutt().getFirst().getHendelseId(), is(notNullValue()));
                    assertThat(result.getLoeslatelse().getFirst().getHendelseId(), is(notNullValue()));
                    assertThat(result.getForventetLoeslatelse().getFirst().getHendelseId(), is(notNullValue()));
                })
                .verifyComplete();
    }

    @Test
    void shouldGenerateDistinctHendelseIdsPerType() {

        var innsettelse = RsInstdataKdi.Innsettelse.builder().build();
        var loeslatelse = RsInstdataKdi.Loeslatelse.builder().build();

        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(innsettelse))
                .loeslatelse(List.of(loeslatelse))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> assertThat(
                        result.getInnsettelse().getFirst().getHendelseId(),
                        is(not(equalTo(result.getLoeslatelse().getFirst().getHendelseId())))))
                .verifyComplete();
    }

    @Test
    void shouldIncrementLoepenummerForMultipleHendelserOfSameType() {

        var hendelse1 = RsInstdataKdi.Innsettelse.builder().build();
        var hendelse2 = RsInstdataKdi.Innsettelse.builder().build();

        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(hendelse1, hendelse2))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> {
                    var id1 = result.getInnsettelse().get(0).getHendelseId();
                    var id2 = result.getInnsettelse().get(1).getHendelseId();
                    assertThat(id1, is(not(equalTo(id2))));
                })
                .verifyComplete();
    }

    @Test
    void shouldSetPubliseringstidspunktOnAnnulleringWhenNull() {

        var annullering = RsInstdataKdi.Annullering.builder().build();
        var kdi = RsInstdataKdi.builder()
                .annullering(List.of(annullering))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result ->
                        assertThat(result.getAnnullering().getFirst().getPubliseringstidspunkt(), is(notNullValue())))
                .verifyComplete();
    }

    @Test
    void shouldPreserveExistingPubliseringstidspunktOnAnnullering() {

        var existing = LocalDateTime.of(2025, 6, 1, 12, 0);
        var annullering = RsInstdataKdi.Annullering.builder()
                .publiseringstidspunkt(existing)
                .build();
        var kdi = RsInstdataKdi.builder()
                .annullering(List.of(annullering))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result ->
                        assertThat(result.getAnnullering().getFirst().getPubliseringstidspunkt(), is(equalTo(existing))))
                .verifyComplete();
    }

    @Test
    void shouldUpdateBestillingInstdataKdiOnOpprettEndre() {

        var kdi = RsInstdataKdi.builder()
                .innsettelse(List.of(RsInstdataKdi.Innsettelse.builder().build()))
                .build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> {
                    assertThat(bestilling.getInstdataKdi(), is(result));
                    assertThat(result.getInnsettelse().getFirst().getHendelseId(), is(notNullValue()));
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleEmptyHendelseLists() {

        var kdi = RsInstdataKdi.builder().build();
        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setInstdataKdi(kdi);

        when(transactionHelperService.persister(eq(BESTILLING_ID), any(RsDollyUtvidetBestilling.class)))
                .thenReturn(Mono.empty());

        StepVerifier.create(instKdiHendelseService.getOppdaterBestilling(bestilling, BESTILLING_ID, true))
                .assertNext(result -> assertThat(result, is(kdi)))
                .verifyComplete();
    }
}
