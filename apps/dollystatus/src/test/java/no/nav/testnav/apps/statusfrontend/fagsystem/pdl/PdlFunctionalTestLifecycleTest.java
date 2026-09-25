package no.nav.testnav.apps.statusfrontend.fagsystem.pdl;

import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdlFunctionalTestLifecycleTest {

    @Mock
    private PdlForvalterClient pdlForvalterClient;
    @Mock
    private PdlProxyClient pdlProxyClient;

    private VirtualTimeScheduler scheduler;
    private PdlFunctionalTestProperties properties;
    private PdlFunctionalTestLifecycle lifecycle;

    @BeforeEach
    void setUp() {
        scheduler = VirtualTimeScheduler.create();
        properties = new PdlFunctionalTestProperties();
        properties.setIdent("03458537037");
        properties.setPollInterval(Duration.ofSeconds(1));
        properties.setPollTimeout(Duration.ofSeconds(5));
        lifecycle = new PdlFunctionalTestLifecycle(
                pdlForvalterClient,
                pdlProxyClient,
                properties,
                scheduler);
    }

    @Test
    void shouldRunOneCreateAndOrderForBothEnvironments() {
        when(pdlForvalterClient.personExists()).thenReturn(Mono.just(false));
        when(pdlForvalterClient.createPerson()).thenReturn(Mono.empty());
        when(pdlForvalterClient.updateName()).thenReturn(Mono.empty());
        when(pdlForvalterClient.sendOrder()).thenReturn(Mono.just(successfulOrder()));
        when(pdlProxyClient.personExists(FunctionalTestEnvironment.Q1, context().runId()))
                .thenReturn(Mono.just(true));
        when(pdlProxyClient.personExists(FunctionalTestEnvironment.Q2, context().runId()))
                .thenReturn(Mono.just(true));
        when(pdlForvalterClient.deletePerson()).thenReturn(Mono.empty());

        StepVerifier.create(lifecycle.preflight(context())
                        .flatMap(preflight -> lifecycle.create(context(), preflight)
                                .flatMap(created -> Mono.when(
                                                lifecycle.verify(
                                                        context(),
                                                        preflight,
                                                        created,
                                                        FunctionalTestEnvironment.Q1),
                                                lifecycle.verify(
                                                        context(),
                                                        preflight,
                                                        created,
                                                        FunctionalTestEnvironment.Q2))
                                        .then(lifecycle.cleanup(
                                                context(),
                                                Optional.of(preflight),
                                                Optional.of(created))))))
                .then(() -> scheduler.advanceTimeBy(Duration.ofSeconds(1)))
                .verifyComplete();

        verify(pdlForvalterClient, times(1)).createPerson();
        verify(pdlForvalterClient, times(1)).updateName();
        verify(pdlForvalterClient, times(1)).sendOrder();
        verify(pdlForvalterClient, times(1)).deletePerson();
    }

    @Test
    void shouldReuseExistingPersonWithoutAddingName() {
        when(pdlForvalterClient.personExists()).thenReturn(Mono.just(true));
        when(pdlForvalterClient.sendOrder()).thenReturn(Mono.just(successfulOrder()));

        StepVerifier.create(lifecycle.preflight(context())
                        .flatMap(preflight -> lifecycle.create(context(), preflight)))
                .expectNext(new PdlCreation(false))
                .verifyComplete();

        verify(pdlForvalterClient, never()).createPerson();
        verify(pdlForvalterClient, never()).updateName();
        verify(pdlForvalterClient).sendOrder();
    }

    @Test
    void shouldLookupPersonBeforeSingleCreateRetry() {
        when(pdlForvalterClient.personExists())
                .thenReturn(Mono.just(false))
                .thenReturn(Mono.just(false));
        when(pdlForvalterClient.createPerson())
                .thenReturn(Mono.error(new TimeoutException()))
                .thenReturn(Mono.empty());
        when(pdlForvalterClient.updateName()).thenReturn(Mono.empty());
        when(pdlForvalterClient.sendOrder()).thenReturn(Mono.just(successfulOrder()));

        StepVerifier.create(lifecycle.preflight(context())
                        .flatMap(preflight -> lifecycle.create(context(), preflight)))
                .expectNext(new PdlCreation(true))
                .verifyComplete();

        InOrder calls = inOrder(pdlForvalterClient);
        calls.verify(pdlForvalterClient).personExists();
        calls.verify(pdlForvalterClient).createPerson();
        calls.verify(pdlForvalterClient).personExists();
        calls.verify(pdlForvalterClient).createPerson();
        verify(pdlForvalterClient, times(2)).createPerson();
    }

    @Test
    void shouldContinueWhenTimedOutCreateProducedPerson() {
        when(pdlForvalterClient.personExists())
                .thenReturn(Mono.just(false))
                .thenReturn(Mono.just(true));
        when(pdlForvalterClient.createPerson()).thenReturn(Mono.error(new TimeoutException()));
        when(pdlForvalterClient.updateName()).thenReturn(Mono.empty());
        when(pdlForvalterClient.sendOrder()).thenReturn(Mono.just(successfulOrder()));

        StepVerifier.create(lifecycle.preflight(context())
                        .flatMap(preflight -> lifecycle.create(context(), preflight)))
                .expectNext(new PdlCreation(true))
                .verifyComplete();

        verify(pdlForvalterClient, times(1)).createPerson();
    }

    @Test
    void shouldFailOrderWhenNestedEventHasErrorStatus() {
        when(pdlForvalterClient.personExists()).thenReturn(Mono.just(true));
        when(pdlForvalterClient.sendOrder()).thenReturn(Mono.just(new PdlOrderResponse(
                new PdlOrderResponse.PersonOrders(List.of(new PdlOrderResponse.Order(
                        List.of(new PdlOrderResponse.Event("FEIL"))))),
                List.of())));

        StepVerifier.create(lifecycle.preflight(context())
                        .flatMap(preflight -> lifecycle.create(context(), preflight)))
                .expectError(IllegalStateException.class)
                .verify();
    }

    @Test
    void shouldTimeoutPollingWithoutSleeping() {
        when(pdlProxyClient.personExists(FunctionalTestEnvironment.Q1, context().runId()))
                .thenReturn(Mono.just(false));

        StepVerifier.withVirtualTime(
                        () -> lifecycle.verify(
                                context(),
                                new PdlPreflight(true),
                                new PdlCreation(false),
                                FunctionalTestEnvironment.Q1),
                        () -> scheduler,
                        Long.MAX_VALUE)
                .thenAwait(Duration.ofSeconds(5))
                .expectError(FunctionalTestVerificationTimeoutException.class)
                .verify();
    }

    @Test
    void shouldWaitForSlowPollBeforeStartingNextRequest() {
        when(pdlProxyClient.personExists(FunctionalTestEnvironment.Q1, context().runId()))
                .thenReturn(Mono.delay(Duration.ofSeconds(2), scheduler).thenReturn(false))
                .thenReturn(Mono.delay(Duration.ofSeconds(1), scheduler).thenReturn(true));

        StepVerifier.withVirtualTime(
                        () -> lifecycle.verify(
                                context(),
                                new PdlPreflight(true),
                                new PdlCreation(false),
                                FunctionalTestEnvironment.Q1),
                        () -> scheduler,
                        Long.MAX_VALUE)
                .thenAwait(Duration.ofSeconds(4))
                .verifyComplete();

        verify(pdlProxyClient, times(2))
                .personExists(FunctionalTestEnvironment.Q1, context().runId());
    }

    @Test
    void shouldDeleteCreatedPerson() {
        when(pdlForvalterClient.deletePerson()).thenReturn(Mono.empty());

        StepVerifier.create(lifecycle.cleanup(
                        context(),
                        Optional.of(new PdlPreflight(false)),
                        Optional.of(new PdlCreation(true))))
                .verifyComplete();

        verify(pdlForvalterClient).deletePerson();
    }

    @Test
    void shouldDeleteExistingReservedPerson() {
        when(pdlForvalterClient.deletePerson()).thenReturn(Mono.empty());

        StepVerifier.create(lifecycle.cleanup(
                        context(),
                        Optional.of(new PdlPreflight(true)),
                        Optional.of(new PdlCreation(false))))
                .verifyComplete();

        verify(pdlForvalterClient).deletePerson();
    }

    @Test
    void shouldNotDeleteWhenPreflightDidNotComplete() {
        StepVerifier.create(lifecycle.cleanup(
                        context(),
                        Optional.empty(),
                        Optional.empty()))
                .verifyComplete();

        verifyNoInteractions(pdlForvalterClient);
    }

    private static PdlOrderResponse successfulOrder() {
        return new PdlOrderResponse(
                new PdlOrderResponse.PersonOrders(List.of(new PdlOrderResponse.Order(
                        List.of(new PdlOrderResponse.Event("OK"))))),
                List.of());
    }

    private static FunctionalTestContext context() {
        return new FunctionalTestContext(
                RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d"),
                new SystemId("pdl"),
                FunctionalTestEnvironment.GLOBAL,
                Instant.parse("2026-09-21T10:00:00Z"));
    }
}
