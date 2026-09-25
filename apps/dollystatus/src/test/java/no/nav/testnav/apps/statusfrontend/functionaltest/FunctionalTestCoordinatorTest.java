package no.nav.testnav.apps.statusfrontend.functionaltest;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestCooldownException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestNotFoundException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunReference;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class FunctionalTestCoordinatorTest {

    private static final Instant STARTED_AT = Instant.parse("2026-09-21T10:00:00Z");

    @Test
    void shouldLogCreateAndCleanupFailuresWithoutSensitiveDetails() {
        var logger = (Logger) LoggerFactory.getLogger(FunctionalTestCoordinator.class);
        var originalLevel = logger.getLevel();
        var appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logger.addAppender(appender);
        logger.setLevel(Level.WARN);

        try {
            var definition = new PhasedDefinition();
            var coordinator = coordinator(definition);
            var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));
            assertThat(runReference).isNotNull();
            var sensitiveDetails = "03458537037 bearer-token raw-response";

            definition.preflightResult.tryEmitValue(new TestValue());
            definition.createResult.tryEmitError(WebClientResponseException.create(
                    400, sensitiveDetails, HttpHeaders.EMPTY, sensitiveDetails.getBytes(UTF_8), UTF_8));
            definition.cleanupResult.tryEmitError(Exceptions.retryExhausted(
                    sensitiveDetails,
                    WebClientResponseException.create(
                            403, sensitiveDetails, HttpHeaders.EMPTY, sensitiveDetails.getBytes(UTF_8), UTF_8)));
            var completed = awaitCompleted(coordinator, runReference);

            assertThat(completed.results()).singleElement()
                    .satisfies(status -> assertThat(status.state()).isEqualTo(FunctionalTestState.CLEANUP_FAILED));
            assertThat(appender.list).hasSize(2)
                    .allSatisfy(event -> {
                        assertThat(event.getFormattedMessage())
                                .contains("runId=" + runReference.runId().value())
                                .doesNotContain("03458537037", "bearer-token", "raw-response");
                        assertThat(event.getThrowableProxy()).isNull();
                    });
            assertThat(appender.list.get(0).getFormattedMessage())
                    .contains("fase=CREATE", "httpStatus=400");
            assertThat(appender.list.get(1).getFormattedMessage())
                    .contains("fase=CLEANUP", "httpStatus=403");
        } finally {
            logger.detachAppender(appender);
            logger.setLevel(originalLevel);
            appender.stop();
        }
    }

    @Test
    void shouldSupportEmptyRegistry() {
        var clock = new MutableClock(STARTED_AT);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of()),
                new FunctionalTestCache(clock),
                clock);

        var statuses = coordinator.getSystemStatuses().block(Duration.ofSeconds(1));
        assertThat(statuses).isEmpty();
        assertThatThrownBy(() -> coordinator.startAllExpired().block(Duration.ofSeconds(1)))
                .isInstanceOf(FunctionalTestNotFoundException.class);
    }

    @Test
    void shouldReturnSameRunIdForConcurrentStart() throws InterruptedException {
        var definition = new GatedPreflightDefinition();
        var coordinator = coordinator(definition);
        var references = Mono.zip(
                        coordinator.startAllExpired(),
                        coordinator.startAllExpired())
                .map(tuple -> List.of(tuple.getT1(), tuple.getT2()))
                .block(Duration.ofSeconds(1));

        assertThat(references).isNotNull();
        assertThat(definition.preflightInvoked.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(references.get(0).runId()).isEqualTo(references.get(1).runId());

        definition.preflightResult.tryEmitValue(new TestValue());
        awaitCompleted(coordinator, references.getFirst());
    }

    @Test
    void shouldUpdateEveryExecutionPhase() throws InterruptedException {
        var definition = new PhasedDefinition();
        var coordinator = coordinator(definition);
        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        assertThat(definition.preflightInvoked.await(1, TimeUnit.SECONDS)).isTrue();
        assertState(coordinator, runReference, FunctionalTestState.PREFLIGHT);

        definition.preflightResult.tryEmitValue(new TestValue());
        assertThat(definition.createInvoked.await(1, TimeUnit.SECONDS)).isTrue();
        assertState(coordinator, runReference, FunctionalTestState.CREATE);

        definition.createResult.tryEmitValue(new TestValue());
        assertThat(definition.verifyInvoked.await(1, TimeUnit.SECONDS)).isTrue();
        assertState(coordinator, runReference, FunctionalTestState.VERIFY);

        definition.verifyResult.tryEmitValue(new TestValue());
        assertThat(definition.cleanupInvoked.await(1, TimeUnit.SECONDS)).isTrue();
        assertState(coordinator, runReference, FunctionalTestState.CLEANUP);

        definition.cleanupResult.tryEmitEmpty();
        var completedRun = awaitCompleted(coordinator, runReference);

        assertThat(completedRun.results()).singleElement()
                .satisfies(status -> {
                    assertThat(status.state()).isEqualTo(FunctionalTestState.OK);
                    assertThat(status.cleanupAttempts()).isEqualTo(1);
                    assertThat(status.completedAt()).isNotNull();
                    assertThat(status.cachedUntil()).isEqualTo(status.completedAt().plus(Duration.ofHours(1)));
                });
    }

    @Test
    void shouldAttemptCleanupAfterVerifyFailureWhenCreateSucceeded() throws InterruptedException {
        var definition = new VerifyFailureDefinition();
        var coordinator = coordinator(definition);
        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        assertThat(definition.cleanupInvoked.await(1, TimeUnit.SECONDS)).isTrue();
        var completedRun = awaitCompleted(coordinator, runReference);

        assertThat(definition.cleanupAttempts).hasValue(1);
        assertThat(completedRun.results()).singleElement()
                .satisfies(status -> {
                    assertThat(status.state()).isEqualTo(FunctionalTestState.VERIFY_FAILED);
                    assertThat(status.cleanupAttempts()).isEqualTo(1);
                    assertThat(status.error().message()).isEqualTo("Testdata kunne ikke verifiseres.");
                });
    }

    @Test
    void shouldRejectManualRunDuringCooldownAndAllowItBeforeTtlExpires() {
        var definition = new CountingDefinition();
        var clock = new MutableClock(STARTED_AT);
        var registry = new FunctionalTestRegistry(List.of(definition));
        var cache = new FunctionalTestCache(clock);
        var coordinator = new FunctionalTestCoordinator(registry, cache, clock);
        var firstRun = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(firstRun).isNotNull();
        awaitCompleted(coordinator, firstRun);

        assertThatThrownBy(() -> coordinator.startSystem(new SystemId("arena"))
                .block(Duration.ofSeconds(1)))
                .isInstanceOf(FunctionalTestCooldownException.class);

        clock.setInstant(STARTED_AT.plus(Duration.ofMinutes(5)));
        var manualRun = coordinator.startSystem(new SystemId("arena")).block(Duration.ofSeconds(1));

        assertThat(manualRun).isNotNull();
        awaitCompleted(coordinator, manualRun);
        assertThat(definition.createAttempts).hasValue(2);
    }

    @Test
    void shouldReleaseRunBeforeFullRunListenerCompletes() throws InterruptedException {
        var definition = new CountingDefinition();
        var clock = new MutableClock(STARTED_AT);
        var listener = mock(FunctionalTestResultListener.class);
        var listenerStarted = new CountDownLatch(1);
        var releaseListener = new CountDownLatch(1);
        doAnswer(_ -> {
            listenerStarted.countDown();
            assertThat(releaseListener.await(5, TimeUnit.SECONDS)).isTrue();
            return null;
        }).when(listener).onFullRunCompleted(any());
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(definition)),
                new FunctionalTestCache(clock),
                clock,
                List.of(),
                List.of(listener));

        try {
            var firstRun = coordinator.startAllExpired().block(Duration.ofSeconds(1));
            assertThat(firstRun).isNotNull();
            assertThat(listenerStarted.await(5, TimeUnit.SECONDS)).isTrue();
            awaitCompleted(coordinator, firstRun);

            assertThatThrownBy(() -> coordinator.startSystem(new SystemId("arena"))
                    .block(Duration.ofSeconds(1)))
                    .isInstanceOf(FunctionalTestCooldownException.class);

            clock.setInstant(STARTED_AT.plus(Duration.ofMinutes(5)));
            var manualRun = coordinator.startSystem(new SystemId("arena")).block(Duration.ofSeconds(1));
            assertThat(manualRun).isNotNull();
            assertThat(manualRun.runId()).isNotEqualTo(firstRun.runId());
            awaitCompleted(coordinator, manualRun);
            assertThat(definition.createAttempts).hasValue(2);
        } finally {
            releaseListener.countDown();
        }
    }

    @Test
    void shouldRetryCleanupThreeTimesAfterTimeout() {
        var definition = new RetryingCleanupDefinition();
        var coordinator = coordinator(definition);
        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        var completedRun = awaitCompleted(coordinator, runReference);

        assertThat(definition.cleanupAttempts).hasValue(4);
        assertThat(completedRun.results()).singleElement()
                .satisfies(status -> {
                    assertThat(status.state()).isEqualTo(FunctionalTestState.CLEANUP_FAILED);
                    assertThat(status.cleanupAttempts()).isEqualTo(4);
                });
    }

    @Test
    void shouldNotRetryCleanupAfterNonRetryableFailure() {
        var definition = new NonRetryingCleanupDefinition();
        var coordinator = coordinator(definition);
        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        var completedRun = awaitCompleted(coordinator, runReference);

        assertThat(definition.cleanupAttempts).hasValue(1);
        assertThat(completedRun.results()).singleElement()
                .satisfies(status -> {
                    assertThat(status.state()).isEqualTo(FunctionalTestState.CLEANUP_FAILED);
                    assertThat(status.cleanupAttempts()).isEqualTo(1);
                });
    }

    @Test
    void shouldKeepPdlEnvironmentStatusesSeparateAndCleanupLastAfterDefinitionFailure() {
        var events = new CopyOnWriteArrayList<String>();
        var pdlLifecycle = new RecordingPdlLifecycle(events, true);
        var definition = new RequiresPdlDefinition(events, true);
        var clock = new MutableClock(STARTED_AT);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(definition)),
                new FunctionalTestCache(clock),
                clock,
                List.of(pdlLifecycle));

        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        var completedRun = awaitCompleted(coordinator, runReference);

        assertThat(pdlLifecycle.createAttempts).hasValue(1);
        assertThat(completedRun.results())
                .filteredOn(status -> status.systemId().equals(new SystemId("pdl")))
                .satisfiesExactlyInAnyOrder(
                        status -> {
                            assertThat(status.environment()).isEqualTo(FunctionalTestEnvironment.Q1);
                            assertThat(status.state()).isEqualTo(FunctionalTestState.OK);
                        },
                        status -> {
                            assertThat(status.environment()).isEqualTo(FunctionalTestEnvironment.Q2);
                            assertThat(status.state()).isEqualTo(FunctionalTestState.VERIFY_FAILED);
                        });
        assertThat(completedRun.results())
                .filteredOn(status -> status.systemId().equals(new SystemId("arena")))
                .satisfiesExactlyInAnyOrder(
                        status -> {
                            assertThat(status.environment()).isEqualTo(FunctionalTestEnvironment.Q1);
                            assertThat(status.state()).isEqualTo(FunctionalTestState.VERIFY_FAILED);
                        },
                        status -> {
                            assertThat(status.environment()).isEqualTo(FunctionalTestEnvironment.Q2);
                            assertThat(status.state()).isEqualTo(FunctionalTestState.BLOCKED);
                        });
        assertThat(events.getLast()).isEqualTo("pdl-cleanup");
        assertThat(events).containsSubsequence("definition-cleanup-Q1", "pdl-cleanup");
    }

    @Test
    void shouldWrapManualRequiringSystemWithOnePdlLifecycle() {
        var events = new CopyOnWriteArrayList<String>();
        var pdlLifecycle = new RecordingPdlLifecycle(events, false);
        var definition = new RequiresPdlDefinition(events, false, Set.of(FunctionalTestEnvironment.Q1));
        var clock = new MutableClock(STARTED_AT);
        var listener = mock(FunctionalTestResultListener.class);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(definition)),
                new FunctionalTestCache(clock),
                clock,
                List.of(pdlLifecycle),
                List.of(listener));

        var runReference = coordinator.startSystem(new SystemId("arena")).block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        awaitCompleted(coordinator, runReference);
        assertThat(pdlLifecycle.createAttempts).hasValue(1);
        assertThat(events)
                .startsWith("pdl-preflight", "pdl-create")
                .containsSubsequence("pdl-verify-Q1", "definition-preflight-Q1")
                .endsWith("pdl-cleanup");
        verify(listener, after(100).never()).onFullRunCompleted(any());
    }

    @Test
    void shouldFollowBackendPhasesAndCleanupPdlLast() {
        var events = new CopyOnWriteArrayList<String>();
        var pdlLifecycle = new RecordingPdlLifecycle(events, false);
        var registry = new FunctionalTestRegistry(
                List.of(
                        new OrderedDefinition(events, "arena"),
                        new OrderedDefinition(events, "pensjon-tp")),
                List.of(new OrderedTechnicalStatus(events, "tags")));
        var clock = new MutableClock(STARTED_AT);
        var listener = mock(FunctionalTestResultListener.class);
        var coordinator = new FunctionalTestCoordinator(
                registry,
                new FunctionalTestCache(clock),
                clock,
                List.of(pdlLifecycle),
                List.of(listener));

        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));

        assertThat(runReference).isNotNull();
        awaitCompleted(coordinator, runReference);
        assertThat(events)
                .containsSubsequence(
                        "tags",
                        "pensjon-tp",
                        "arena",
                        "pdl-cleanup");
        assertThat(events.getLast()).isEqualTo("pdl-cleanup");
        var completedRunCaptor = ArgumentCaptor.forClass(FunctionalTestRunStatus.class);
        verify(listener, timeout(1000)).onFullRunCompleted(completedRunCaptor.capture());
        assertThat(completedRunCaptor.getValue().state()).isEqualTo(FunctionalTestRunState.COMPLETED);
        assertThat(completedRunCaptor.getValue().results())
                .hasSize(5)
                .allSatisfy(status -> assertThat(status.state())
                        .isIn(FunctionalTestState.OK, FunctionalTestState.TECHNICAL_ONLY));
    }

    @Test
    void shouldNotNotifyFullRunWhenSomeChecksAreCached() {
        var definition = new CountingDefinition();
        var technicalCheck = new TechnicalStatusDefinition() {
            @Override
            public TechnicalStatusDescriptor descriptor() {
                return new TechnicalStatusDescriptor(
                        new SystemId("aareg"),
                        new DisplayName("AAREG"),
                        Set.of(FunctionalTestEnvironment.GLOBAL));
            }

            @Override
            public Mono<Void> check(FunctionalTestContext context) {
                return Mono.empty();
            }
        };
        var clock = new MutableClock(STARTED_AT);
        var listener = mock(FunctionalTestResultListener.class);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(definition), List.of(technicalCheck)),
                new FunctionalTestCache(clock),
                clock,
                List.of(),
                List.of(listener));

        var manualRun = coordinator.startSystem(new SystemId("arena")).block(Duration.ofSeconds(1));
        assertThat(manualRun).isNotNull();
        awaitCompleted(coordinator, manualRun);
        verify(listener, after(100).never()).onFullRunCompleted(any());
        var partialRun = coordinator.startAllExpired().block(Duration.ofSeconds(1));
        assertThat(partialRun).isNotNull();
        var completed = awaitCompleted(coordinator, partialRun);

        assertThat(completed.results()).singleElement()
                .satisfies(status -> assertThat(status.systemId()).isEqualTo(new SystemId("aareg")));
        verify(listener, after(100).never()).onFullRunCompleted(any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldBlockTechnicalCheckWhenPdlIsMissingOrFailed(boolean configuredPdl) {
        var events = new CopyOnWriteArrayList<String>();
        var clock = new MutableClock(STARTED_AT);
        List<PdlTestLifecycle<?, ?>> lifecycles = configuredPdl
                ? List.of(new RecordingPdlLifecycle(events, true))
                : List.of();
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(), List.of(new OrderedTechnicalStatus(events, "tags"))),
                new FunctionalTestCache(clock),
                clock,
                lifecycles);

        var runReference = coordinator.startAllExpired().block(Duration.ofSeconds(1));
        assertThat(runReference).isNotNull();
        var completed = awaitCompleted(coordinator, runReference);

        assertThat(events).doesNotContain("tags");
        assertThat(completed.results())
                .filteredOn(status -> status.systemId().equals(new SystemId("tags")))
                .singleElement()
                .satisfies(status -> assertThat(status.state()).isEqualTo(FunctionalTestState.BLOCKED));
    }

    @Test
    void shouldRunFourGroupsConcurrentlyAndShareTheLimitWithTechnicalChecks() throws InterruptedException {
        var events = new CopyOnWriteArrayList<String>();
        var started = new LinkedBlockingQueue<String>();
        var definitions = List.of(
                new GatedCleanupDefinition("brregstub", Set.of(FunctionalTestEnvironment.GLOBAL), events, started),
                new GatedCleanupDefinition("instdata", Set.of(FunctionalTestEnvironment.GLOBAL), events, started),
                new GatedCleanupDefinition("krr", Set.of(FunctionalTestEnvironment.GLOBAL), events, started),
                new GatedCleanupDefinition("skattekort", Set.of(FunctionalTestEnvironment.GLOBAL), events, started),
                new GatedCleanupDefinition("udi", Set.of(FunctionalTestEnvironment.GLOBAL), events, started));
        var technicalGate = Sinks.<Void>empty();
        var technicalCheck = new TechnicalStatusDefinition() {
            @Override
            public TechnicalStatusDescriptor descriptor() {
                return new TechnicalStatusDescriptor(
                        new SystemId("medl"), new DisplayName("MEDL"), Set.of(FunctionalTestEnvironment.GLOBAL));
            }

            @Override
            public Mono<Void> check(FunctionalTestContext context) {
                started.add("medl-GLOBAL");
                return technicalGate.asMono();
            }
        };
        var clock = new MutableClock(STARTED_AT);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.copyOf(definitions), List.of(technicalCheck)),
                new FunctionalTestCache(clock), clock);
        try {
            var run = coordinator.startAllExpired().block(Duration.ofSeconds(1));
            assertThat(run).isNotNull();
            for (var definition : definitions.subList(0, 4)) {
                assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo(definition.systemId + "-GLOBAL");
            }
            assertThat(started).isEmpty();
            assertThat(coordinator.startAllExpired().block(Duration.ofSeconds(1))).isEqualTo(run);
            var statuses = coordinator.getRun(run.runId()).block(Duration.ofSeconds(1));
            assertThat(statuses).isNotNull();
            assertThat(statuses.results().subList(0, 4))
                    .allSatisfy(status -> assertThat(status.state()).isEqualTo(FunctionalTestState.CLEANUP));
            assertThat(statuses.results().subList(4, 6))
                    .allSatisfy(status -> assertThat(status.state()).isEqualTo(FunctionalTestState.RUNNING));

            definitions.getFirst().cleanupGate.tryEmitError(new IllegalStateException("Cleanup failed"));
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo("udi-GLOBAL");
            assertThat(started).isEmpty();
            definitions.get(1).cleanupGate.tryEmitEmpty();
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo("medl-GLOBAL");
            definitions.forEach(definition -> definition.cleanupGate.tryEmitEmpty());
            technicalGate.tryEmitEmpty();

            var completed = awaitCompleted(coordinator, run);
            assertThat(completed.results()).hasSize(6);
            assertThat(completed.results().getFirst().state()).isEqualTo(FunctionalTestState.CLEANUP_FAILED);
            assertThat(completed.results().subList(1, 5))
                    .allSatisfy(status -> assertThat(status.state()).isEqualTo(FunctionalTestState.OK));
            assertThat(completed.results().getLast().state()).isEqualTo(FunctionalTestState.TECHNICAL_ONLY);
        } finally {
            definitions.forEach(definition -> definition.cleanupGate.tryEmitEmpty());
            technicalGate.tryEmitEmpty();
        }
    }

    @ParameterizedTest
    @CsvSource({
            "pensjon-afp-offentlig,pensjon-tp,inntektstub",
            "arbeidssoekerregisteret,arena,instdata",
            "nom,skjermingsregister,instdata",
            "nom,tps-messaging-egenansatt,instdata",
            "skjermingsregister,tps-messaging-egenansatt,instdata"
    })
    void shouldSerializeRelatedSystemsAndTheirEnvironments(
            String firstSystem, String secondSystem, String independentSystem) throws InterruptedException {
        var events = new CopyOnWriteArrayList<String>();
        var started = new LinkedBlockingQueue<String>();
        var first = new GatedCleanupDefinition(
                firstSystem, Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2), events, started);
        var second = new GatedCleanupDefinition(
                secondSystem, Set.of(FunctionalTestEnvironment.GLOBAL), events, started);
        var independent = new GatedCleanupDefinition(
                independentSystem, Set.of(FunctionalTestEnvironment.GLOBAL), events, started);
        var clock = new MutableClock(STARTED_AT);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(first, second, independent)),
                new FunctionalTestCache(clock), clock);
        try {
            var run = coordinator.startAllExpired().block(Duration.ofSeconds(1));
            assertThat(run).isNotNull();
            assertThat(List.of(started.poll(5, TimeUnit.SECONDS), started.poll(5, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(firstSystem + "-Q1", independentSystem + "-GLOBAL");
            assertThat(started).isEmpty();
            first.cleanupGate.tryEmitEmpty();
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo(firstSystem + "-Q2");
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo(secondSystem + "-GLOBAL");
            second.cleanupGate.tryEmitEmpty();
            independent.cleanupGate.tryEmitEmpty();
            awaitCompleted(coordinator, run);
            assertThat(events).containsSubsequence(
                    firstSystem + "-Q1-cleanup", firstSystem + "-Q2-start",
                    firstSystem + "-Q2-cleanup", secondSystem + "-GLOBAL-start");
        } finally {
            first.cleanupGate.tryEmitEmpty();
            second.cleanupGate.tryEmitEmpty();
            independent.cleanupGate.tryEmitEmpty();
        }
    }

    @Test
    void shouldWaitForEachPhaseAndAllGroupCleanupBeforeFinalPdlCleanup() throws InterruptedException {
        var events = new CopyOnWriteArrayList<String>();
        var started = new LinkedBlockingQueue<String>();
        var pension = new GatedCleanupDefinition(
                "pensjon-tp", Set.of(FunctionalTestEnvironment.Q1), events, started);
        var arena = new GatedCleanupDefinition(
                "arena", Set.of(FunctionalTestEnvironment.Q1), events, started);
        var instdata = new GatedCleanupDefinition(
                "instdata", Set.of(FunctionalTestEnvironment.Q1), events, started);
        var clock = new MutableClock(STARTED_AT);
        var listener = mock(FunctionalTestResultListener.class);
        var coordinator = new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(pension, arena, instdata),
                        List.of(new OrderedTechnicalStatus(events, "tags"))),
                new FunctionalTestCache(clock), clock,
                List.of(new RecordingPdlLifecycle(events, false)), List.of(listener));
        try {
            var run = coordinator.startAllExpired().block(Duration.ofSeconds(1));
            assertThat(run).isNotNull();
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo("pensjon-tp-Q1");
            assertThat(started).isEmpty();
            assertThat(events).containsSubsequence("pdl-create", "tags", "pensjon-tp-Q1-start");
            assertThat(events).doesNotContain("pdl-cleanup");

            pension.cleanupGate.tryEmitEmpty();
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo("arena-Q1");
            assertThat(started.poll(5, TimeUnit.SECONDS)).isEqualTo("instdata-Q1");
            arena.cleanupGate.tryEmitEmpty();
            assertThat(events).doesNotContain("pdl-cleanup");
            verify(listener, org.mockito.Mockito.never()).onFullRunCompleted(any());
            instdata.cleanupGate.tryEmitEmpty();
            awaitCompleted(coordinator, run);

            assertThat(events).containsSubsequence("pensjon-tp-Q1-cleanup", "arena-Q1-start");
            assertThat(events.getLast()).isEqualTo("pdl-cleanup");
            verify(listener, timeout(1000)).onFullRunCompleted(any());
        } finally {
            pension.cleanupGate.tryEmitEmpty();
            arena.cleanupGate.tryEmitEmpty();
            instdata.cleanupGate.tryEmitEmpty();
        }
    }

    private static FunctionalTestCoordinator coordinator(FunctionalTestDefinition<?, ?, ?> definition) {
        var clock = new MutableClock(STARTED_AT);
        var registry = new FunctionalTestRegistry(List.of(definition));
        return new FunctionalTestCoordinator(registry, new FunctionalTestCache(clock), clock);
    }

    private static void assertState(
            FunctionalTestCoordinator coordinator,
            RunReference runReference,
            FunctionalTestState expectedState
    ) {
        var run = coordinator.getRun(runReference.runId()).block(Duration.ofSeconds(1));

        assertThat(run).isNotNull();
        assertThat(run.results()).singleElement()
                .extracting(status -> status.state())
                .isEqualTo(expectedState);
    }

    private static FunctionalTestRunStatus awaitCompleted(
            FunctionalTestCoordinator coordinator,
            RunReference runReference
    ) {
        var deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (System.nanoTime() < deadline) {
            var run = coordinator.getRun(runReference.runId()).block(Duration.ofSeconds(1));
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

    private static FunctionalTestDescriptor descriptor() {
        return new FunctionalTestDescriptor(
                new SystemId("arena"),
                new DisplayName("Arena"),
                Set.of(FunctionalTestEnvironment.Q1),
                CleanupExpectation.DELETED);
    }

    private record TestValue() {
    }

    private static class GatedPreflightDefinition
            implements FunctionalTestDefinition<TestValue, TestValue, TestValue> {

        protected final CountDownLatch preflightInvoked = new CountDownLatch(1);
        protected final Sinks.One<TestValue> preflightResult = Sinks.one();

        @Override
        public FunctionalTestDescriptor descriptor() {
            return FunctionalTestCoordinatorTest.descriptor();
        }

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            preflightInvoked.countDown();
            return preflightResult.asMono();
        }

        @Override
        public Mono<TestValue> create(FunctionalTestContext context, TestValue preflightResult) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> verify(
                FunctionalTestContext context,
                TestValue preflightResult,
                TestValue createResult
        ) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            return Mono.empty();
        }
    }

    private static final class PhasedDefinition extends GatedPreflightDefinition {

        private final CountDownLatch createInvoked = new CountDownLatch(1);
        private final CountDownLatch verifyInvoked = new CountDownLatch(1);
        private final CountDownLatch cleanupInvoked = new CountDownLatch(1);
        private final Sinks.One<TestValue> createResult = Sinks.one();
        private final Sinks.One<TestValue> verifyResult = Sinks.one();
        private final Sinks.Empty<Void> cleanupResult = Sinks.empty();

        @Override
        public Mono<TestValue> create(FunctionalTestContext context, TestValue preflightResult) {
            createInvoked.countDown();
            return createResult.asMono();
        }

        @Override
        public Mono<TestValue> verify(
                FunctionalTestContext context,
                TestValue preflightResult,
                TestValue createResult
        ) {
            verifyInvoked.countDown();
            return verifyResult.asMono();
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            cleanupInvoked.countDown();
            return cleanupResult.asMono();
        }
    }

    private static final class GatedCleanupDefinition extends GatedPreflightDefinition {

        private final String systemId;
        private final Set<FunctionalTestEnvironment> environments;
        private final List<String> events;
        private final LinkedBlockingQueue<String> started;
        private final Sinks.Empty<Void> cleanupGate = Sinks.empty();

        private GatedCleanupDefinition(
                String systemId,
                Set<FunctionalTestEnvironment> environments,
                List<String> events,
                LinkedBlockingQueue<String> started
        ) {
            this.systemId = systemId;
            this.environments = environments;
            this.events = events;
            this.started = started;
        }

        @Override
        public FunctionalTestDescriptor descriptor() {
            return new FunctionalTestDescriptor(
                    new SystemId(systemId), new DisplayName(systemId), environments, CleanupExpectation.DELETED);
        }

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            events.add(systemId + "-" + context.environment() + "-start");
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            started.add(systemId + "-" + context.environment());
            return cleanupGate.asMono()
                    .doOnSuccess(_ -> events.add(systemId + "-" + context.environment() + "-cleanup"));
        }
    }

    private static final class VerifyFailureDefinition extends GatedPreflightDefinition {

        private final CountDownLatch cleanupInvoked = new CountDownLatch(1);
        private final AtomicInteger cleanupAttempts = new AtomicInteger();

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> verify(
                FunctionalTestContext context,
                TestValue preflightResult,
                TestValue createResult
        ) {
            return Mono.error(new IllegalStateException("Sensitive downstream detail"));
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            cleanupAttempts.incrementAndGet();
            cleanupInvoked.countDown();
            return Mono.empty();
        }
    }

    private static final class CountingDefinition extends GatedPreflightDefinition {

        private final AtomicInteger createAttempts = new AtomicInteger();

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> create(FunctionalTestContext context, TestValue preflightResult) {
            createAttempts.incrementAndGet();
            return Mono.just(new TestValue());
        }
    }

    private static final class RetryingCleanupDefinition extends GatedPreflightDefinition {

        private final AtomicInteger cleanupAttempts = new AtomicInteger();

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            cleanupAttempts.incrementAndGet();
            return Mono.error(new TimeoutException("Sensitive downstream detail"));
        }
    }

    private static final class NonRetryingCleanupDefinition extends GatedPreflightDefinition {

        private final AtomicInteger cleanupAttempts = new AtomicInteger();

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            cleanupAttempts.incrementAndGet();
            return Mono.error(new IllegalStateException("Sensitive downstream detail"));
        }
    }

    private static final class RecordingPdlLifecycle implements PdlTestLifecycle<TestValue, TestValue> {

        private final List<String> events;
        private final boolean failQ2;
        private final AtomicInteger createAttempts = new AtomicInteger();

        private RecordingPdlLifecycle(List<String> events, boolean failQ2) {
            this.events = events;
            this.failQ2 = failQ2;
        }

        @Override
        public FunctionalTestDescriptor descriptor() {
            return new FunctionalTestDescriptor(
                    new SystemId("pdl"),
                    new DisplayName("PDL"),
                    Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2),
                    CleanupExpectation.DELETED);
        }

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            events.add("pdl-preflight");
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> create(FunctionalTestContext context, TestValue preflightResult) {
            events.add("pdl-create");
            createAttempts.incrementAndGet();
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> verify(
                FunctionalTestContext context,
                TestValue preflightResult,
                TestValue createResult,
                FunctionalTestEnvironment environment
        ) {
            events.add("pdl-verify-" + environment);
            return failQ2 && environment == FunctionalTestEnvironment.Q2
                    ? Mono.error(new IllegalStateException("Sensitive downstream detail"))
                    : Mono.empty();
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                Optional<TestValue> preflightResult,
                Optional<TestValue> createResult
        ) {
            events.add("pdl-cleanup");
            return Mono.empty();
        }
    }

    private static final class RequiresPdlDefinition
            implements FunctionalTestDefinition<TestValue, TestValue, TestValue> {

        private final List<String> events;
        private final boolean failVerification;
        private final Set<FunctionalTestEnvironment> environments;

        private RequiresPdlDefinition(List<String> events, boolean failVerification) {
            this(events, failVerification, Set.of(
                    FunctionalTestEnvironment.Q1,
                    FunctionalTestEnvironment.Q2));
        }

        private RequiresPdlDefinition(
                List<String> events,
                boolean failVerification,
                Set<FunctionalTestEnvironment> environments
        ) {
            this.events = events;
            this.failVerification = failVerification;
            this.environments = environments;
        }

        @Override
        public FunctionalTestDescriptor descriptor() {
            return new FunctionalTestDescriptor(
                    new SystemId("arena"),
                    new DisplayName("Arena"),
                    environments,
                    CleanupExpectation.DELETED);
        }

        @Override
        public boolean requiresPdl() {
            return true;
        }

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            events.add("definition-preflight-" + context.environment());
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> create(FunctionalTestContext context, TestValue preflightResult) {
            events.add("definition-create-" + context.environment());
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> verify(
                FunctionalTestContext context,
                TestValue preflightResult,
                TestValue createResult
        ) {
            events.add("definition-verify-" + context.environment());
            return failVerification
                    ? Mono.error(new IllegalStateException("Sensitive downstream detail"))
                    : Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            events.add("definition-cleanup-" + context.environment());
            return Mono.empty();
        }
    }

    private static final class OrderedDefinition
            implements FunctionalTestDefinition<TestValue, TestValue, TestValue> {

        private final List<String> events;
        private final SystemId systemId;

        private OrderedDefinition(List<String> events, String systemId) {
            this.events = events;
            this.systemId = new SystemId(systemId);
        }

        @Override
        public FunctionalTestDescriptor descriptor() {
            return new FunctionalTestDescriptor(
                    systemId,
                    new DisplayName(systemId.value()),
                    Set.of(FunctionalTestEnvironment.Q1),
                    CleanupExpectation.DELETED);
        }

        @Override
        public boolean requiresPdl() {
            return true;
        }

        @Override
        public Mono<TestValue> preflight(FunctionalTestContext context) {
            events.add(systemId.value());
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> create(FunctionalTestContext context, TestValue preflightResult) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<TestValue> verify(
                FunctionalTestContext context,
                TestValue preflightResult,
                TestValue createResult
        ) {
            return Mono.just(new TestValue());
        }

        @Override
        public Mono<Void> cleanup(
                FunctionalTestContext context,
                TestValue preflightResult,
                Optional<TestValue> createResult,
                Optional<TestValue> verificationResult,
                CleanupExpectation expectedEndState
        ) {
            return Mono.empty();
        }
    }

    private static final class OrderedTechnicalStatus implements TechnicalStatusDefinition {

        private final List<String> events;
        private final SystemId systemId;

        private OrderedTechnicalStatus(List<String> events, String systemId) {
            this.events = events;
            this.systemId = new SystemId(systemId);
        }

        @Override
        public TechnicalStatusDescriptor descriptor() {
            return new TechnicalStatusDescriptor(
                    systemId,
                    new DisplayName(systemId.value()),
                    Set.of(FunctionalTestEnvironment.GLOBAL));
        }

        @Override
        public boolean requiresPdl() {
            return true;
        }

        @Override
        public Mono<Void> check(FunctionalTestContext context) {
            events.add(systemId.value());
            return Mono.empty();
        }
    }
}
