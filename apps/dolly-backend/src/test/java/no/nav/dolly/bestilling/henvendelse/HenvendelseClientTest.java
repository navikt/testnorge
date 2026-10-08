package no.nav.dolly.bestilling.henvendelse;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseMeldingRequest;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseResponse;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.henvendelse.RsHenvendelse;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static no.nav.dolly.domain.resultset.SystemTyper.HENVENDELSE;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HenvendelseClientTest {

    private static final String IDENT = "12345678901";
    private static final String AKTOR_ID = "aktor-id";
    private static final String ENHET = "1234";
    private static final String KJEDE_ID = "kjede-id";

    @Mock
    private HenvendelseConsumer henvendelseConsumer;

    @Mock
    private MapperFacade mapperFacade;

    @Mock
    private Norg2Consumer norg2Consumer;

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private TransactionHelperService transactionHelperService;

    @InjectMocks
    private HenvendelseClient henvendelseClient;

    @Test
    void shouldCompleteWithoutCallingDependenciesWhenHenvendelseIsNull() {

        StepVerifier.create(henvendelseClient.gjenopprett(
                        new RsDollyUtvidetBestilling(),
                        DollyPerson.builder().ident(IDENT).build(),
                        new BestillingProgress(),
                        true))
                .verifyComplete();

        verify(personServiceConsumer, never()).getPdlPersoner(anyList());
        verify(henvendelseConsumer, never()).sendHenvendelse(any());
        verify(transactionHelperService, never()).persister(any(), any(), anyString());
    }

    @Test
    void shouldMapAndSendMessagesAndSamtalereferaterWithAktorAndNorgUnit() {

        var progress = new BestillingProgress();
        var statusCaptor = ArgumentCaptor.forClass(String.class);
        var bestilling = bestillingMedHenvendelse(new RsHenvendelse(
                List.of(new RsHenvendelse.Melding("tema", "tema", null, "meldingstekst", null, null)),
                List.of(new RsHenvendelse.Samtalereferat("tema", "tema", null, "referattekst", null))));
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT)))
                .thenReturn(Flux.just(pdlPersonBolk(true, true)));
        when(norg2Consumer.getNorgEnhet("0301"))
                .thenReturn(Mono.just(Norg2EnhetResponse.builder().enhetNr(ENHET).build()));
        when(mapperFacade.map(any(RsHenvendelse.Melding.class), eq(HenvendelseMeldingRequest.class), any(MappingContext.class)))
                .thenAnswer(invocation -> {
                    var melding = (RsHenvendelse.Melding) invocation.getArgument(0);
                    var context = (MappingContext) invocation.getArgument(2);
                    var request = new HenvendelseMeldingRequest();
                    request.setAktorId((String) context.getProperty("aktorId"));
                    request.setTemagruppe(melding.getTemagruppe());
                    request.setTema(melding.getTema());
                    request.setEnhet((String) context.getProperty("enhet"));
                    request.setFritekst(melding.getFritekst());
                    request.setKjedeId(melding.getKjedeId());
                    request.setType("melding");
                    return request;
                });
        when(mapperFacade.map(any(RsHenvendelse.Samtalereferat.class), eq(HenvendelseSamtalereferatRequest.class), any(MappingContext.class)))
                .thenAnswer(invocation -> {
                    var referat = (RsHenvendelse.Samtalereferat) invocation.getArgument(0);
                    var context = (MappingContext) invocation.getArgument(2);
                    var request = new HenvendelseSamtalereferatRequest();
                    request.setAktorId((String) context.getProperty("aktorId"));
                    request.setTemagruppe(referat.getTemagruppe());
                    request.setTema(referat.getTema());
                    request.setEnhet((String) context.getProperty("enhet"));
                    request.setFritekst(referat.getFritekst());
                    request.setKjedeId(referat.getKjedeId());
                    request.setType("samtalereferat");
                    return request;
                });
        when(henvendelseConsumer.sendHenvendelse(any()))
                .thenReturn(
                        Mono.just(HenvendelseResponse.builder()
                                .status(HttpStatus.OK)
                                .type("MELDING")
                                .build()),
                        Mono.just(HenvendelseResponse.builder()
                                .status(HttpStatus.OK)
                                .type("SAMTALEREFERAT")
                                .build()));
        when(transactionHelperService.persister(eq(progress), any(), anyString()))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(henvendelseClient.gjenopprett(
                        bestilling,
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        true))
                .expectNext(progress)
                .verifyComplete();

        verify(personServiceConsumer).getPdlPersoner(List.of(IDENT));
        verify(norg2Consumer).getNorgEnhet("0301");
        var requestCaptor = ArgumentCaptor.forClass(HenvendelseSamtalereferatRequest.class);
        verify(henvendelseConsumer, times(2)).sendHenvendelse(requestCaptor.capture());
        assertThat(requestCaptor.getAllValues())
                .allSatisfy(request -> {
                    assertThat(request.getAktorId()).isEqualTo(AKTOR_ID);
                    assertThat(request.getEnhet()).isEqualTo(ENHET);
                })
                .extracting(HenvendelseSamtalereferatRequest::getType)
                .containsExactlyInAnyOrder("melding", "samtalereferat");
        verify(transactionHelperService, times(2)).persister(eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo(getInfoVenter(HENVENDELSE.getBeskrivelse()));
        assertThat(statusCaptor.getValue())
                .contains("MELDING: OK")
                .contains("SAMTALEREFERAT: OK");
        var inOrder = inOrder(transactionHelperService, personServiceConsumer);
        inOrder.verify(transactionHelperService).persister(
                eq(progress), any(), eq(getInfoVenter(HENVENDELSE.getBeskrivelse())));
        inOrder.verify(personServiceConsumer).getPdlPersoner(List.of(IDENT));
    }

    @Test
    void shouldUseFallbackUnitAndPersistErrorStatusWhenAktorExistsWithoutGeography() {

        var progress = new BestillingProgress();
        var statusCaptor = ArgumentCaptor.forClass(String.class);
        var bestilling = bestillingMedHenvendelse(new RsHenvendelse(
                List.of(new RsHenvendelse.Melding("tema", "tema", null, "meldingstekst", null, null)),
                List.of()));
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT)))
                .thenReturn(Flux.just(pdlPersonBolk(true, false)));
        when(mapperFacade.map(any(RsHenvendelse.Melding.class), eq(HenvendelseMeldingRequest.class), any(MappingContext.class)))
                .thenAnswer(invocation -> {
                    var context = (MappingContext) invocation.getArgument(2);
                    var request = new HenvendelseMeldingRequest();
                    request.setAktorId((String) context.getProperty("aktorId"));
                    request.setEnhet((String) context.getProperty("enhet"));
                    return request;
                });
        when(henvendelseConsumer.sendHenvendelse(any()))
                .thenReturn(Mono.just(HenvendelseResponse.builder()
                        .status(HttpStatus.BAD_REQUEST)
                        .type("MELDING")
                        .feilmelding("ugyldig: melding")
                        .build()));
        when(transactionHelperService.persister(eq(progress), any(), anyString()))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(henvendelseClient.gjenopprett(
                        bestilling,
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        true))
                .expectNext(progress)
                .verifyComplete();

        verify(norg2Consumer, never()).getNorgEnhet(anyString());
        var requestCaptor = ArgumentCaptor.forClass(HenvendelseSamtalereferatRequest.class);
        verify(henvendelseConsumer).sendHenvendelse(requestCaptor.capture());
        assertThat(requestCaptor.getValue().getAktorId()).isEqualTo(AKTOR_ID);
        assertThat(requestCaptor.getValue().getEnhet()).isEqualTo("0315");
        verify(transactionHelperService, times(2)).persister(eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo(getInfoVenter(HENVENDELSE.getBeskrivelse()));
        assertThat(statusCaptor.getValue()).isEqualTo("MELDING: Feil= ugyldig= melding");
    }

    @Test
    void shouldSkipCreationWhenNoCurrentAktorIdExists() {

        var progress = new BestillingProgress();
        var statusCaptor = ArgumentCaptor.forClass(String.class);
        var bestilling = bestillingMedHenvendelse(new RsHenvendelse(
                List.of(new RsHenvendelse.Melding("tema", "tema", null, "meldingstekst", null, null)),
                List.of()));
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT)))
                .thenReturn(Flux.just(pdlPersonBolk(false, false)));
        when(transactionHelperService.persister(eq(progress), any(), anyString()))
                .thenReturn(Mono.just(progress));

        StepVerifier.create(henvendelseClient.gjenopprett(
                        bestilling,
                        DollyPerson.builder().ident(IDENT).build(),
                        progress,
                        true))
                .expectNext(progress)
                .verifyComplete();

        verify(henvendelseConsumer, never()).sendHenvendelse(any());
        verify(transactionHelperService, times(2)).persister(eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getFirst()).isEqualTo(getInfoVenter(HENVENDELSE.getBeskrivelse()));
        assertThat(statusCaptor.getValue()).isEmpty();
    }

    @Test
    void shouldDeleteHenvendelserForCurrentAktorIdsOnRelease() {

        when(personServiceConsumer.getPdlPersoner(List.of(IDENT)))
                .thenReturn(Flux.just(pdlPersonBolk(true, false)));
        when(henvendelseConsumer.getHenvendelse(AKTOR_ID))
                .thenReturn(Mono.just(HenvendelseResponse.builder()
                        .data(List.of(
                                HenvendelseResponse.Info.builder()
                                        .henvendelseType("MELDINGSKJEDE")
                                        .kjedeId(KJEDE_ID)
                                        .build(),
                                HenvendelseResponse.Info.builder()
                                        .henvendelseType("MELDING")
                                        .kjedeId("melding-id")
                                        .build()))
                        .build()));
        when(henvendelseConsumer.deleteHenvendelse(KJEDE_ID))
                .thenReturn(Mono.just(HenvendelseResponse.builder().status(HttpStatus.OK).build()));

        henvendelseClient.release(List.of(IDENT));

        verify(personServiceConsumer).getPdlPersoner(List.of(IDENT));
        verify(henvendelseConsumer).getHenvendelse(AKTOR_ID);
        verify(henvendelseConsumer).deleteHenvendelse(KJEDE_ID);
        verify(henvendelseConsumer, never()).deleteHenvendelse("melding-id");
    }

    private static RsDollyUtvidetBestilling bestillingMedHenvendelse(RsHenvendelse henvendelse) {

        var bestilling = new RsDollyUtvidetBestilling();
        bestilling.setHenvendelse(henvendelse);
        return bestilling;
    }

    private static PdlPersonBolk pdlPersonBolk(boolean hasCurrentAktorId, boolean hasGeography) {

        var identer = hasCurrentAktorId ?
                List.of(
                        new PdlPersonBolk.Identinformasjon(AKTOR_ID, "AKTORID", false),
                        new PdlPersonBolk.Identinformasjon("historisk-aktor", "AKTORID", true)) :
                List.of(new PdlPersonBolk.Identinformasjon("historisk-aktor", "AKTORID", true));
        var data = PdlPersonBolk.Data.builder()
                .hentIdenterBolk(List.of(new PdlPersonBolk.IdenterBolk(IDENT, identer)))
                .build();
        if (hasGeography) {
            data.setHentGeografiskTilknytningBolk(List.of(
                    PdlPersonBolk.GeografiskTilknytningBolk.builder()
                            .geografiskTilknytning(PdlPersonBolk.GeografiskTilknytning.builder()
                                    .gtKommune("0301")
                                    .build())
                            .build()));
        }
        return PdlPersonBolk.builder().data(data).build();
    }
}
