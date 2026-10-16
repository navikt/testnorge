package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.PdlPerson;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransactionHelperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonServiceClientTest {

    private static final String IDENT = "01010101010";

    @Mock
    private PersonServiceConsumer personServiceConsumer;

    @Mock
    private ErrorStatusDecoder errorStatusDecoder;

    @Mock
    private TransactionHelperService transactionHelperService;

    @Mock
    private JsonMapper jsonMapper;

    @Mock
    private ApplicationConfig applicationConfig;

    @InjectMocks
    private PersonServiceClient personServiceClient;

    @Test
    void shouldSetPdlSyncWhenPersonExists() {

        var progress = new BestillingProgress();
        var dollyPerson = dollyPerson();
        stubProgress(progress);
        when(applicationConfig.getClientTimeout()).thenReturn(5L);
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.empty());
        when(personServiceConsumer.isPerson(IDENT, Set.of())).thenReturn(Mono.just(
                PersonServiceResponse.builder()
                        .ident(IDENT)
                        .status(HttpStatus.OK)
                        .exists(true)
                        .build()));

        StepVerifier.withVirtualTime(() -> personServiceClient.syncPerson(dollyPerson, progress))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(result -> {
                    assertThat(result).isSameAs(progress);
                    assertThat(result.isPdlSync()).isTrue();
                })
                .verifyComplete();

        var statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(transactionHelperService, times(2)).persister(
                eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getFirst())
                .isEqualTo("Info: Synkronisering mot PDL startet ...");
        assertThat(statusCaptor.getAllValues().getLast())
                .startsWith("Synkronisering mot PDL tok ");
    }

    @Test
    void shouldSetErrorStatusWhenPersonServiceReturnsInternalServerError() {

        var progress = new BestillingProgress();
        var dollyPerson = dollyPerson();
        stubProgress(progress);
        when(applicationConfig.getClientTimeout()).thenReturn(5L);
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.empty());
        when(personServiceConsumer.isPerson(IDENT, Set.of())).thenReturn(Mono.just(
                PersonServiceResponse.builder()
                        .ident(IDENT)
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .feilmelding("Internal Server Error")
                        .exists(false)
                        .build()));
        when(errorStatusDecoder.getErrorText(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error"))
                .thenReturn("Internal Server Error");

        StepVerifier.withVirtualTime(() -> personServiceClient.syncPerson(dollyPerson, progress))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(result -> {
                    assertThat(result).isSameAs(progress);
                    assertThat(result.isPdlSync()).isFalse();
                })
                .verifyComplete();

        var statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(transactionHelperService, times(2)).persister(
                eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast())
                .isEqualTo("Feil: Synkronisering mot PDL gitt på grunn av 500 INTERNAL_SERVER_ERROR.");
    }

    @Test
    void shouldHandleInternalServerErrorThrownByPersonService() {

        var progress = new BestillingProgress();
        var dollyPerson = dollyPerson();
        var internalServerError = WebClientResponseException.create(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                null,
                null,
                null);
        stubProgress(progress);
        when(applicationConfig.getClientTimeout()).thenReturn(5L);
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.empty());
        when(personServiceConsumer.isPerson(IDENT, Set.of())).thenReturn(Mono.error(internalServerError));
        when(errorStatusDecoder.getErrorText(
                eq(HttpStatus.INTERNAL_SERVER_ERROR), anyString()))
                .thenReturn("Internal Server Error");

        StepVerifier.withVirtualTime(() -> personServiceClient.syncPerson(dollyPerson, progress))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(result -> {
                    assertThat(result).isSameAs(progress);
                    assertThat(result.isPdlSync()).isFalse();
                })
                .verifyComplete();

        var statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(transactionHelperService, times(2)).persister(
                eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast())
                .isEqualTo("Feil: Synkronisering mot PDL gitt på grunn av 500 INTERNAL_SERVER_ERROR.");
    }

    @ParameterizedTest
    @MethodSource("responsesWithoutRelatedPersons")
    void shouldSynchronizeMainPersonWhenPdlResponseHasNoRelatedPersons(PdlPersonBolk response) {

        var progress = new BestillingProgress();
        stubProgress(progress);
        when(applicationConfig.getClientTimeout()).thenReturn(5L);
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.just(response));
        when(personServiceConsumer.isPerson(IDENT, Set.of())).thenReturn(Mono.just(
                PersonServiceResponse.builder()
                        .ident(IDENT)
                        .status(HttpStatus.OK)
                        .exists(true)
                        .build()));

        StepVerifier.withVirtualTime(() -> personServiceClient.syncPerson(dollyPerson(), progress))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(result -> {
                    assertThat(result).isSameAs(progress);
                    assertThat(result.isPdlSync()).isTrue();
                })
                .verifyComplete();

        verify(personServiceConsumer).isPerson(IDENT, Set.of());
        var statusCaptor = ArgumentCaptor.forClass(String.class);
        verify(transactionHelperService, times(2)).persister(eq(progress), any(), statusCaptor.capture());
        assertThat(statusCaptor.getAllValues().getLast()).startsWith("Synkronisering mot PDL tok ");
        verifyNoInteractions(errorStatusDecoder);
    }

    @Test
    void shouldSynchronizeRelatedPersonWhenPdlResponseContainsPersonData() {

        var relatedIdent = "02020202020";
        var person = new PdlPerson.Person();
        var relation = new PdlPerson.ForelderBarnRelasjon();
        relation.setRelatertPersonsIdent(relatedIdent);
        person.getForelderBarnRelasjon().add(relation);
        var response = PdlPersonBolk.builder()
                .data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder().person(person).build()))
                        .build())
                .build();
        var progress = new BestillingProgress();
        stubProgress(progress);
        when(applicationConfig.getClientTimeout()).thenReturn(5L);
        when(personServiceConsumer.getPdlPersoner(List.of(IDENT))).thenReturn(Flux.just(response));
        for (var ident : List.of(IDENT, relatedIdent)) {
            when(personServiceConsumer.isPerson(ident, Set.of())).thenReturn(Mono.just(
                    PersonServiceResponse.builder().ident(ident).status(HttpStatus.OK).exists(true).build()));
        }

        StepVerifier.withVirtualTime(() -> personServiceClient.syncPerson(dollyPerson(), progress))
                .thenAwait(Duration.ofSeconds(1))
                .assertNext(result -> {
                    assertThat(result).isSameAs(progress);
                    assertThat(result.isPdlSync()).isTrue();
                })
                .verifyComplete();

        verify(personServiceConsumer).isPerson(IDENT, Set.of());
        verify(personServiceConsumer).isPerson(relatedIdent, Set.of());
        verify(transactionHelperService, times(2)).persister(eq(progress), any(), anyString());
        verifyNoInteractions(errorStatusDecoder);
    }

    private static Stream<PdlPersonBolk> responsesWithoutRelatedPersons() {
        return Stream.of(
                PdlPersonBolk.builder().build(),
                PdlPersonBolk.builder()
                        .data(PdlPersonBolk.Data.builder().hentPersonBolk(null).build()).build(),
                PdlPersonBolk.builder().data(PdlPersonBolk.Data.builder().build()).build(),
                PdlPersonBolk.builder().data(PdlPersonBolk.Data.builder()
                        .hentPersonBolk(List.of(PdlPersonBolk.PersonBolk.builder().build())).build()).build());
    }

    private void stubProgress(BestillingProgress progress) {
        when(transactionHelperService.persister(eq(progress), any(), anyString()))
                .thenReturn(Mono.just(progress));
    }

    private DollyPerson dollyPerson() {
        return DollyPerson.builder()
                .ident(IDENT)
                .master(Testident.Master.PDL)
                .build();
    }
}
