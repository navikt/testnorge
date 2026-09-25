package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PensjonFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCache;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCoordinator;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestRegistry;
import no.nav.testnav.apps.statusfrontend.functionaltest.PdlTestLifecycle;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestExistingDataException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment.Q1;
import static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment.Q2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PensjonFunctionalTestLifecycleTest {

    private static final Instant STARTED_AT = Instant.parse("2026-09-21T10:00:00Z");
    private static final RunId RUN_ID = RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");

    @Mock
    private PensjonClient client;

    private VirtualTimeScheduler scheduler;
    private PensjonFunctionalTestProperties properties;

    @BeforeEach
    void setUp() {
        scheduler = VirtualTimeScheduler.create();
        properties = new PensjonFunctionalTestProperties();
        properties.setPollInterval(Duration.ofSeconds(1));
        properties.setPollTimeout(Duration.ofSeconds(5));
    }

    @ParameterizedTest
    @CsvSource({"tp,Q1", "tp,Q2", "popp,Q1", "popp,Q2", "afp,Q1", "afp,Q2", "avtale,GLOBAL"})
    void shouldCleanupExistingPensjonDataBeforeNewPreflight(
            String system, FunctionalTestEnvironment environment) {
        var existing = Mono.just(new PensjonResourceStatus(false, false));
        var empty = Mono.just(PensjonResourceStatus.emptyStatus());
        AbstractPensjonFunctionalTest lifecycle = switch (system) {
            case "tp" -> {
                when(client.getTpForhold(environment, RUN_ID)).thenReturn(existing, empty);
                when(client.deleteTpForhold(environment, RUN_ID)).thenReturn(Mono.empty());
                yield new TpForholdFunctionalTest(client, properties, scheduler);
            }
            case "popp" -> {
                when(client.getPopp(environment, RUN_ID)).thenReturn(existing, empty);
                when(client.deletePopp(environment, RUN_ID)).thenReturn(Mono.empty());
                yield new PoppFunctionalTest(client, properties, scheduler);
            }
            case "afp" -> {
                when(client.getAfpOffentlig(environment, RUN_ID)).thenReturn(existing, empty);
                when(client.deleteAfpOffentlig(environment, RUN_ID)).thenReturn(Mono.empty());
                yield new AfpOffentligFunctionalTest(client, properties, scheduler);
            }
            case "avtale" -> {
                when(client.getPensjonsavtale(Q1, RUN_ID)).thenReturn(existing, empty);
                when(client.getPensjonsavtale(Q2, RUN_ID)).thenReturn(empty);
                when(client.deletePensjonsavtale(RUN_ID)).thenReturn(Mono.empty());
                yield new PensjonsavtaleFunctionalTest(client, properties, scheduler);
            }
            default -> throw new IllegalArgumentException(system);
        };
        var context = context(lifecycle.descriptor().systemId().value(), environment);

        StepVerifier.create(lifecycle.preflight(context))
                .expectError(FunctionalTestExistingDataException.class).verify();
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();

        switch (system) {
            case "tp" -> verify(client).deleteTpForhold(environment, RUN_ID);
            case "popp" -> verify(client).deletePopp(environment, RUN_ID);
            case "afp" -> verify(client).deleteAfpOffentlig(environment, RUN_ID);
            case "avtale" -> {
                verify(client).deletePensjonsavtale(RUN_ID);
                verify(client, times(3)).getPensjonsavtale(Q1, RUN_ID);
                verify(client, times(3)).getPensjonsavtale(Q2, RUN_ID);
            }
            default -> throw new IllegalArgumentException(system);
        }
    }

    @Test
    void shouldCreateVerifyCleanupAndAfterCheckTpForhold() {
        when(client.getTpForhold(Q1, RUN_ID))
                .thenReturn(
                        Mono.just(PensjonResourceStatus.emptyStatus()),
                        Mono.just(new PensjonResourceStatus(false, true)),
                        Mono.just(PensjonResourceStatus.emptyStatus()));
        when(client.createTpForhold(Q1, RUN_ID)).thenReturn(Mono.empty());
        when(client.deleteTpForhold(Q1, RUN_ID)).thenReturn(Mono.empty());
        var lifecycle = new TpForholdFunctionalTest(client, properties, scheduler);

        StepVerifier.withVirtualTime(() -> lifecycle.preflight(context("pensjon-tp", Q1))
                        .flatMap(preflight -> lifecycle.create(context("pensjon-tp", Q1), preflight)
                                .flatMap(created -> lifecycle.verify(
                                                context("pensjon-tp", Q1),
                                                preflight,
                                                created)
                                        .flatMap(verified -> lifecycle.cleanup(
                                                context("pensjon-tp", Q1),
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                lifecycle.descriptor().expectedCleanupState())))),
                        () -> scheduler,
                        Long.MAX_VALUE)
                .thenAwait(Duration.ofSeconds(1))
                .verifyComplete();

        InOrder calls = inOrder(client);
        calls.verify(client).getTpForhold(Q1, RUN_ID);
        calls.verify(client).createTpForhold(Q1, RUN_ID);
        calls.verify(client).getTpForhold(Q1, RUN_ID);
        calls.verify(client).deleteTpForhold(Q1, RUN_ID);
        calls.verify(client).getTpForhold(Q1, RUN_ID);
    }

    @Test
    void shouldVerifyPensjonsavtaleInBothEnvironmentsButRegisterGlobalStatus() {
        when(client.getPensjonsavtale(Q1, RUN_ID))
                .thenReturn(
                        Mono.just(PensjonResourceStatus.emptyStatus()),
                        Mono.just(new PensjonResourceStatus(false, true)),
                        Mono.just(PensjonResourceStatus.emptyStatus()));
        when(client.getPensjonsavtale(Q2, RUN_ID))
                .thenReturn(
                        Mono.just(PensjonResourceStatus.emptyStatus()),
                        Mono.just(new PensjonResourceStatus(false, true)),
                        Mono.just(PensjonResourceStatus.emptyStatus()));
        when(client.createPensjonsavtale(RUN_ID)).thenReturn(Mono.empty());
        when(client.deletePensjonsavtale(RUN_ID)).thenReturn(Mono.empty());
        var lifecycle = new PensjonsavtaleFunctionalTest(client, properties, scheduler);

        StepVerifier.withVirtualTime(() -> lifecycle.preflight(context("pensjon-pensjonsavtale", FunctionalTestEnvironment.GLOBAL))
                        .flatMap(preflight -> lifecycle.create(
                                        context("pensjon-pensjonsavtale", FunctionalTestEnvironment.GLOBAL),
                                        preflight)
                                .flatMap(created -> lifecycle.verify(
                                                context("pensjon-pensjonsavtale", FunctionalTestEnvironment.GLOBAL),
                                                preflight,
                                                created)
                                        .flatMap(verified -> lifecycle.cleanup(
                                                context("pensjon-pensjonsavtale", FunctionalTestEnvironment.GLOBAL),
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                lifecycle.descriptor().expectedCleanupState())))),
                        () -> scheduler,
                        Long.MAX_VALUE)
                .thenAwait(Duration.ofSeconds(1))
                .verifyComplete();

        assertThat(lifecycle.descriptor().environments())
                .containsExactly(FunctionalTestEnvironment.GLOBAL);
        InOrder calls = inOrder(client);
        calls.verify(client).getPensjonsavtale(Q1, RUN_ID);
        calls.verify(client).getPensjonsavtale(Q2, RUN_ID);
        calls.verify(client).createPensjonsavtale(RUN_ID);
        calls.verify(client).getPensjonsavtale(Q1, RUN_ID);
        calls.verify(client).getPensjonsavtale(Q2, RUN_ID);
        calls.verify(client).deletePensjonsavtale(RUN_ID);
        calls.verify(client).getPensjonsavtale(Q1, RUN_ID);
        calls.verify(client).getPensjonsavtale(Q2, RUN_ID);
    }

    @Test
    void shouldRunQ1ThenQ2CleanupAfterVerifyFailureAndPdlLast() {
        var events = new CopyOnWriteArrayList<String>();
        var q1Lookups = new AtomicInteger();
        var q2Lookups = new AtomicInteger();
        when(client.getTpForhold(eq(Q1), any())).thenAnswer(_ -> Mono.defer(() -> {
            var call = q1Lookups.incrementAndGet();
            events.add("tp-get-q1-" + call);
            return switch (call) {
                case 1, 3 -> Mono.just(PensjonResourceStatus.emptyStatus());
                default -> Mono.error(new IllegalStateException("Verification failed."));
            };
        }));
        when(client.getTpForhold(eq(Q2), any())).thenAnswer(_ -> Mono.defer(() -> {
            var call = q2Lookups.incrementAndGet();
            events.add("tp-get-q2-" + call);
            return call == 1 || call == 3
                    ? Mono.just(PensjonResourceStatus.emptyStatus())
                    : Mono.just(new PensjonResourceStatus(false, true));
        }));
        when(client.createTpForhold(any(), any())).thenAnswer(invocation -> Mono.fromRunnable(() ->
                events.add("tp-create-" + environmentName(invocation.getArgument(0)))));
        when(client.deleteTpForhold(any(), any())).thenAnswer(invocation -> Mono.fromRunnable(() ->
                events.add("tp-delete-" + environmentName(invocation.getArgument(0)))));

        var lifecycle = new TpForholdFunctionalTest(client, properties, scheduler);
        var clock = Clock.fixed(STARTED_AT, java.time.ZoneOffset.UTC);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(lifecycle)),
                new FunctionalTestCache(clock),
                clock,
                List.of(new RecordingPdlLifecycle(events)));

        var runReference = coordinator.startSystem(new SystemId("pensjon-tp"))
                .block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        var completedRun = awaitCompleted(coordinator, runReference.runId());

        assertThat(completedRun.results())
                .filteredOn(status -> status.systemId().equals(new SystemId("pensjon-tp")))
                .satisfiesExactly(
                        status -> {
                            assertThat(status.environment()).isEqualTo(Q1);
                            assertThat(status.state()).isEqualTo(FunctionalTestState.VERIFY_FAILED);
                        },
                        status -> {
                            assertThat(status.environment()).isEqualTo(Q2);
                            assertThat(status.state()).isEqualTo(FunctionalTestState.OK);
                        });
        assertThat(events).containsSubsequence(
                "tp-create-q1",
                "tp-delete-q1",
                "tp-create-q2",
                "tp-delete-q2");
        assertThat(events.getLast()).isEqualTo("pdl-cleanup");
        verify(client, times(1)).deleteTpForhold(eq(Q1), any());
    }

    @Test
    void shouldRegisterOnlySupportedPensjonEnvironmentModels() {
        var registry = new FunctionalTestRegistry(List.of(
                new TpForholdFunctionalTest(client, properties, scheduler),
                new PoppFunctionalTest(client, properties, scheduler),
                new AfpOffentligFunctionalTest(client, properties, scheduler),
                new PensjonsavtaleFunctionalTest(client, properties, scheduler)));

        assertThat(registry.registrations())
                .extracting(registration -> registration.key().systemId().value()
                        + ":" + registration.environment().name())
                .containsExactly(
                        "pensjon-afp-offentlig:Q1",
                        "pensjon-afp-offentlig:Q2",
                        "pensjon-pensjonsavtale:GLOBAL",
                        "pensjon-popp:Q1",
                        "pensjon-popp:Q2",
                        "pensjon-tp:Q1",
                        "pensjon-tp:Q2");
    }

    private static FunctionalTestContext context(
            String systemId,
            FunctionalTestEnvironment environment
    ) {
        return new FunctionalTestContext(
                RUN_ID,
                new SystemId(systemId),
                environment,
                STARTED_AT);
    }

    private static String environmentName(FunctionalTestEnvironment environment) {
        return environment.name().toLowerCase(java.util.Locale.ROOT);
    }

    private static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus awaitCompleted(
            FunctionalTestCoordinator coordinator,
            RunId runId
    ) {
        var deadline = System.nanoTime() + Duration.ofSeconds(2).toNanos();
        while (System.nanoTime() < deadline) {
            var run = coordinator.getRun(runId).block(Duration.ofSeconds(1));
            if (run != null && run.state() == FunctionalTestRunState.COMPLETED) {
                return run;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for test run.", exception);
            }
        }
        throw new AssertionError("Test run did not complete.");
    }

    private static final class RecordingPdlLifecycle implements PdlTestLifecycle<String, String> {

        private final CopyOnWriteArrayList<String> events;

        private RecordingPdlLifecycle(CopyOnWriteArrayList<String> events) {
            this.events = events;
        }

        @Override
        public no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor descriptor() {
            return new no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor(
                    new SystemId("pdl"),
                    new no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName("PDL"),
                    java.util.Set.of(Q1, Q2),
                    no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation.DELETED);
        }

        @Override
        public Mono<String> preflight(FunctionalTestContext context) {
            return Mono.fromSupplier(() -> {
                events.add("pdl-preflight");
                return "preflight";
            });
        }

        @Override
        public Mono<String> create(FunctionalTestContext context, String preflightResult) {
            return Mono.fromSupplier(() -> {
                events.add("pdl-create");
                return "created";
            });
        }

        @Override
        public Mono<Void> verify(
                FunctionalTestContext context,
                String preflightResult,
                String createResult,
                FunctionalTestEnvironment environment
        ) {
            return Mono.fromRunnable(() -> events.add("pdl-verify-" + environmentName(environment)));
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                Optional<String> preflightResult,
                Optional<String> createResult
        ) {
            return Mono.fromRunnable(() -> events.add("pdl-cleanup"));
        }
    }
}
