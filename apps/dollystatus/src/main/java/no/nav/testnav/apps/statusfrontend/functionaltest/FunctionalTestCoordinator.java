package no.nav.testnav.apps.statusfrontend.functionaltest;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestCooldownException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestNotFoundException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestRunInProgressException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestRunNotFoundException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunReference;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.ContextView;
import reactor.util.retry.Retry;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestErrorSanitizer.FailurePhase.CLEANUP;
import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestErrorSanitizer.FailurePhase.CREATE;
import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestErrorSanitizer.FailurePhase.PREFLIGHT;
import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestErrorSanitizer.FailurePhase.VERIFY;

@Slf4j
@Service
public class FunctionalTestCoordinator {

    private final Object runMonitor = new Object();
    private final FunctionalTestRegistry registry;
    private final FunctionalTestCache cache;
    private final Clock clock;
    private final List<PdlTestLifecycle<?, ?>> pdlLifecycles;
    private final List<FunctionalTestResultListener> resultListeners;
    private final Map<RunId, ActiveRun> runs = new ConcurrentHashMap<>();
    private ActiveRun activeRun;

    @Autowired
    public FunctionalTestCoordinator(
            FunctionalTestRegistry registry,
            FunctionalTestCache cache,
            Clock clock,
            List<PdlTestLifecycle<?, ?>> pdlLifecycles,
            List<FunctionalTestResultListener> resultListeners
    ) {
        this.registry = registry;
        this.cache = cache;
        this.clock = clock;
        if (pdlLifecycles.size() > 1) {
            throw new IllegalStateException("Flere PDL-livssykluser er registrert.");
        }
        this.pdlLifecycles = List.copyOf(pdlLifecycles);
        this.resultListeners = List.copyOf(resultListeners);
    }

    public FunctionalTestCoordinator(
            FunctionalTestRegistry registry,
            FunctionalTestCache cache,
            Clock clock,
            List<PdlTestLifecycle<?, ?>> pdlLifecycles
    ) {
        this(registry, cache, clock, pdlLifecycles, List.of());
    }

    public FunctionalTestCoordinator(FunctionalTestRegistry registry, FunctionalTestCache cache, Clock clock) {
        this(registry, cache, clock, List.of(), List.of());
    }

    public Mono<List<FunctionalTestStatus>> getSystemStatuses() {
        return Mono.fromSupplier(() -> {
            var statuses = new ArrayList<FunctionalTestStatus>();
            pdlLifecycles.forEach(lifecycle -> lifecycle.descriptor().environments().stream()
                    .sorted()
                    .map(environment -> cache.statusFor(lifecycle.descriptor(), environment))
                    .forEach(statuses::add));
            registry.registrations().stream()
                    .map(cache::statusFor)
                    .forEach(statuses::add);
            registry.technicalRegistrations().stream()
                    .map(cache::statusFor)
                    .forEach(statuses::add);
            return List.copyOf(statuses);
        });
    }

    public Mono<RunReference> startAllExpired() {
        return Mono.deferContextual(contextView -> Mono.fromSupplier(() -> {
            synchronized (runMonitor) {
                if (activeRun != null) {
                    return new RunReference(activeRun.runId());
                }

                var registrations = registry.registrations().stream()
                        .filter(registration -> cache.isExpired(registration.key()))
                        .toList();
                var technicalRegistrations = registry.technicalRegistrations().stream()
                        .filter(registration -> cache.isExpired(registration.key()))
                        .toList();
                var pdlLifecycle = pdlLifecycleFor(registrations.stream()
                        .anyMatch(registration -> registration.definition().requiresPdl())
                        || technicalRegistrations.stream()
                        .anyMatch(registration -> registration.definition().requiresPdl())
                        || isPdlExpired());
                if (registrations.isEmpty() && technicalRegistrations.isEmpty() && pdlLifecycle.isEmpty()) {
                    throw new FunctionalTestNotFoundException();
                }
                return launch(registrations, technicalRegistrations, pdlLifecycle, contextView);
            }
        }));
    }

    public Mono<RunReference> startSystem(SystemId systemId) {
        return Mono.deferContextual(contextView -> Mono.fromSupplier(() -> {
            synchronized (runMonitor) {
                if (activeRun != null) {
                    throw new FunctionalTestRunInProgressException(activeRun.runId());
                }

                var pdlLifecycle = pdlLifecycles.stream()
                        .filter(lifecycle -> lifecycle.descriptor().systemId().equals(systemId))
                        .findFirst();
                if (pdlLifecycle.isPresent()) {
                    verifyCooldown(pdlKeys(pdlLifecycle.get()));
                    return launch(List.of(), List.of(), pdlLifecycle, contextView);
                }

                var registrations = registry.registrationsFor(systemId);
                var technicalRegistrations = registry.technicalRegistrationsFor(systemId);
                if (registrations.isEmpty() && technicalRegistrations.isEmpty()) {
                    throw new FunctionalTestNotFoundException();
                }
                verifyCooldown(Stream.concat(
                                registrations.stream()
                                        .map(FunctionalTestRegistry.RegisteredFunctionalTest::key),
                                technicalRegistrations.stream()
                                        .map(FunctionalTestRegistry.RegisteredTechnicalStatus::key))
                        .toList());
                var requiredPdlLifecycle = pdlLifecycleFor(
                        registrations.stream()
                                .anyMatch(registration -> registration.definition().requiresPdl())
                                || technicalRegistrations.stream()
                                .anyMatch(registration -> registration.definition().requiresPdl()));
                return launch(registrations, technicalRegistrations, requiredPdlLifecycle, contextView);
            }
        }));
    }

    private void verifyCooldown(List<FunctionalTestKey> keys) {
        var retryAfter = keys.stream()
                        .map(cache::cooldownUntil)
                        .flatMap(Optional::stream)
                        .max(Instant::compareTo);
        if (retryAfter.isPresent()) {
            throw new FunctionalTestCooldownException(retryAfter.get());
        }
    }

    public Mono<FunctionalTestRunStatus> getRun(RunId runId) {
        return Mono.fromSupplier(() -> Optional.ofNullable(runs.get(runId))
                .map(ActiveRun::snapshot)
                .orElseThrow(FunctionalTestRunNotFoundException::new));
    }

    private RunReference launch(
            List<FunctionalTestRegistry.RegisteredFunctionalTest> registrations,
            List<FunctionalTestRegistry.RegisteredTechnicalStatus> technicalRegistrations,
            Optional<PdlTestLifecycle<?, ?>> pdlLifecycle,
            ContextView contextView
    ) {
        var runId = RunId.random();
        var startedAt = clock.instant();
        var orderedKeys = new ArrayList<FunctionalTestKey>();
        pdlLifecycle.ifPresent(lifecycle -> orderedKeys.addAll(pdlKeys(lifecycle)));
        orderedKeys.addAll(registrations.stream()
                .map(FunctionalTestRegistry.RegisteredFunctionalTest::key)
                .toList());
        orderedKeys.addAll(technicalRegistrations.stream()
                .map(FunctionalTestRegistry.RegisteredTechnicalStatus::key)
                .toList());
        var run = new ActiveRun(runId, startedAt, orderedKeys);
        pdlLifecycle.ifPresent(lifecycle -> lifecycle.descriptor().environments().forEach(environment ->
                run.update(cache.start(
                        new FunctionalTestKey(lifecycle.descriptor().systemId(), environment),
                        lifecycle.descriptor().displayName(),
                        runId,
                        startedAt))));
        registrations.forEach(registration -> run.update(cache.start(
                registration.key(),
                registration.definition().descriptor().displayName(),
                runId,
                startedAt)));
        technicalRegistrations.forEach(registration -> run.update(cache.start(
                registration.key(),
                registration.definition().descriptor().displayName(),
                runId,
                startedAt)));
        runs.put(runId, run);
        activeRun = run;

        executeRegistrations(registrations, technicalRegistrations, pdlLifecycle, run)
                .subscribeOn(Schedulers.boundedElastic())
                .contextWrite(context -> context.putAll(contextView))
                .subscribe(
                        ignored -> {
                        },
                        throwable -> failRun(run, throwable),
                        () -> completeRun(run));

        return new RunReference(runId);
    }

    private Mono<Void> executeRegistrations(
            List<FunctionalTestRegistry.RegisteredFunctionalTest> registrations,
            List<FunctionalTestRegistry.RegisteredTechnicalStatus> technicalRegistrations,
            Optional<PdlTestLifecycle<?, ?>> pdlLifecycle,
            ActiveRun run
    ) {
        if (pdlLifecycle.isPresent()) {
            return executeWithPdl(pdlLifecycle.get(), registrations, technicalRegistrations, run);
        }
        return executeInBackendOrder(
                registrations,
                technicalRegistrations,
                registration -> registration.definition().requiresPdl()
                        ? blockRegistration(registration, run)
                        : execute(registration, run),
                registration -> registration.definition().requiresPdl()
                        ? completeTechnical(registration, TechnicalStatusState.DOWN, run)
                        : executeTechnical(registration, run));
    }

    private Optional<PdlTestLifecycle<?, ?>> pdlLifecycleFor(boolean needed) {
        return needed ? pdlLifecycles.stream().findFirst() : Optional.empty();
    }

    private boolean isPdlExpired() {
        return pdlLifecycles.stream()
                .findFirst()
                .map(this::pdlKeys)
                .stream()
                .flatMap(List::stream)
                .anyMatch(cache::isExpired);
    }

    private List<FunctionalTestKey> pdlKeys(PdlTestLifecycle<?, ?> lifecycle) {
        return lifecycle.descriptor().environments().stream()
                .sorted()
                .map(environment -> new FunctionalTestKey(lifecycle.descriptor().systemId(), environment))
                .toList();
    }

    private Mono<Void> blockRegistration(
            FunctionalTestRegistry.RegisteredFunctionalTest registration,
            ActiveRun run
    ) {
        return completeFailure(
                run,
                registration.key(),
                FunctionalTestState.BLOCKED,
                0,
                FunctionalTestErrorSanitizer.sanitize(
                        PREFLIGHT,
                        new FunctionalTestBlockedException()));
    }

    private Mono<Void> executeWithPdl(
            PdlTestLifecycle<?, ?> lifecycle,
            List<FunctionalTestRegistry.RegisteredFunctionalTest> registrations,
            List<FunctionalTestRegistry.RegisteredTechnicalStatus> technicalRegistrations,
            ActiveRun run
    ) {
        return executeWithPdlCaptured(lifecycle, registrations, technicalRegistrations, run);
    }

    private <P, C> Mono<Void> executeWithPdlCaptured(
            PdlTestLifecycle<P, C> lifecycle,
            List<FunctionalTestRegistry.RegisteredFunctionalTest> registrations,
            List<FunctionalTestRegistry.RegisteredTechnicalStatus> technicalRegistrations,
            ActiveRun run
    ) {
        return setupPdl(lifecycle, run)
                .flatMap(execution -> executeInBackendOrder(
                                registrations,
                                technicalRegistrations,
                                registration -> registration.definition().requiresPdl()
                                        && !execution.isReadyFor(registration.environment())
                                        ? blockRegistration(registration, run)
                                        : execute(registration, run),
                                registration -> registration.definition().requiresPdl()
                                        && !execution.isReadyFor(registration.environment())
                                        ? completeTechnical(registration, TechnicalStatusState.DOWN, run)
                                        : executeTechnical(registration, run))
                        .then(cleanupPdl(lifecycle, execution, run)));
    }

    private Mono<Void> executeInBackendOrder(
            List<FunctionalTestRegistry.RegisteredFunctionalTest> registrations,
            List<FunctionalTestRegistry.RegisteredTechnicalStatus> technicalRegistrations,
            Function<FunctionalTestRegistry.RegisteredFunctionalTest, Mono<Void>> functionalExecution,
            Function<FunctionalTestRegistry.RegisteredTechnicalStatus, Mono<Void>> technicalExecution
    ) {
        return Flux.range(1, 3)
                .concatMap(phase -> Flux.concat(
                        Flux.fromIterable(registrations)
                                .filter(registration -> executionPhase(registration.key().systemId()) == phase)
                                .concatMap(functionalExecution),
                        Flux.fromIterable(technicalRegistrations)
                                .filter(registration -> executionPhase(registration.key().systemId()) == phase)
                                .concatMap(technicalExecution)))
                .then();
    }

    private int executionPhase(SystemId systemId) {
        var value = systemId.value();
        if ("tags".equals(value)) {
            return 1;
        }
        if ("kontoregister".equals(value)
                || value.startsWith("pensjon-")
                || "aareg".equals(value)
                || "inntektstub".equals(value)) {
            return 2;
        }
        return 3;
    }

    private Mono<Void> executeTechnical(
            FunctionalTestRegistry.RegisteredTechnicalStatus registration,
            ActiveRun run
    ) {
        var context = new FunctionalTestContext(
                run.runId(),
                registration.key().systemId(),
                registration.environment(),
                run.startedAt());
        return Mono.defer(() -> registration.definition().check(context))
                .then(completeTechnical(registration, TechnicalStatusState.UP, run))
                .onErrorResume(_ -> completeTechnical(registration, TechnicalStatusState.DOWN, run));
    }

    private Mono<Void> completeTechnical(
            FunctionalTestRegistry.RegisteredTechnicalStatus registration,
            TechnicalStatusState state,
            ActiveRun run
    ) {
        return Mono.fromRunnable(() -> cache.completeTechnical(
                                registration.key(),
                                run.runId(),
                                state,
                                clock.instant())
                        .ifPresent(status -> recordCompletion(run, status)));
    }

    private <P, C> Mono<PdlExecution<P, C>> setupPdl(
            PdlTestLifecycle<P, C> lifecycle,
            ActiveRun run
    ) {
        var context = pdlContext(lifecycle, run);
        updatePdl(run, lifecycle, FunctionalTestState.PREFLIGHT, null);

        return Mono.defer(() -> lifecycle.preflight(context))
                .switchIfEmpty(Mono.error(new IllegalStateException("PDL preflight returned no result.")))
                .map(preflight -> new PdlExecution<P, C>(
                        Optional.of(preflight),
                        Optional.empty(),
                        Map.of()))
                .onErrorResume(throwable -> Mono.just(failedPdlExecution(
                        lifecycle,
                        throwable instanceof FunctionalTestBlockedException
                                ? FunctionalTestState.BLOCKED
                                : FunctionalTestState.PREFLIGHT_FAILED,
                        FunctionalTestErrorSanitizer.sanitize(PREFLIGHT, throwable))))
                .flatMap(execution -> execution.preflightResult().isEmpty()
                        ? Mono.just(execution)
                        : createPdl(lifecycle, context, execution.preflightResult().get(), run));
    }

    private <P, C> Mono<PdlExecution<P, C>> createPdl(
            PdlTestLifecycle<P, C> lifecycle,
            FunctionalTestContext context,
            P preflightResult,
            ActiveRun run
    ) {
        updatePdl(run, lifecycle, FunctionalTestState.CREATE, null);

        return Mono.defer(() -> lifecycle.create(context, preflightResult))
                .switchIfEmpty(Mono.error(new IllegalStateException("PDL create returned no result.")))
                .map(createResult -> new PdlExecution<>(
                        Optional.of(preflightResult),
                        Optional.of(createResult),
                        Map.<no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment, CompletionOutcome>of()))
                .onErrorResume(throwable -> Mono.just(new PdlExecution<>(
                        Optional.of(preflightResult),
                        Optional.empty(),
                        failedPdlOutcomes(
                                lifecycle,
                                FunctionalTestState.CREATE_FAILED,
                                FunctionalTestErrorSanitizer.sanitize(CREATE, throwable)))))
                .flatMap(execution -> execution.createResult().isEmpty()
                        ? Mono.just(execution)
                        : verifyPdl(
                                lifecycle,
                                context,
                                preflightResult,
                                execution.createResult().get(),
                                run));
    }

    private <P, C> Mono<PdlExecution<P, C>> verifyPdl(
            PdlTestLifecycle<P, C> lifecycle,
            FunctionalTestContext context,
            P preflightResult,
            C createResult,
            ActiveRun run
    ) {
        return Flux.fromIterable(lifecycle.descriptor().environments())
                .flatMap(environment -> {
                    var key = new FunctionalTestKey(lifecycle.descriptor().systemId(), environment);
                    update(run, key, FunctionalTestState.VERIFY, 0, null);
                    return Mono.defer(() -> lifecycle.verify(
                                    context,
                                    preflightResult,
                                    createResult,
                                    environment))
                            .thenReturn(Map.entry(environment, new CompletionOutcome(FunctionalTestState.OK, null)))
                            .onErrorResume(throwable -> {
                                var state = throwable instanceof FunctionalTestVerificationTimeoutException
                                        || throwable instanceof TimeoutException
                                        ? FunctionalTestState.VERIFY_TIMEOUT
                                        : FunctionalTestState.VERIFY_FAILED;
                                var error = FunctionalTestErrorSanitizer.sanitize(VERIFY, throwable);
                                update(run, key, state, 0, error);
                                return Mono.just(Map.entry(environment, new CompletionOutcome(state, error)));
                            });
                }, lifecycle.descriptor().environments().size())
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .map(outcomes -> new PdlExecution<>(
                        Optional.of(preflightResult),
                        Optional.of(createResult),
                        outcomes));
    }

    private <P, C> Mono<Void> cleanupPdl(
            PdlTestLifecycle<P, C> lifecycle,
            PdlExecution<P, C> execution,
            ActiveRun run
    ) {
        lifecycle.descriptor().environments().forEach(environment -> {
            var key = new FunctionalTestKey(lifecycle.descriptor().systemId(), environment);
            var outcome = execution.outcomes().get(environment);
            update(run, key, FunctionalTestState.CLEANUP, 1, outcome == null ? null : outcome.error());
        });

        var cleanupAttempts = new AtomicInteger();
        return Mono.defer(() -> {
                    cleanupAttempts.incrementAndGet();
                    return lifecycle.cleanup(
                            pdlContext(lifecycle, run),
                            execution.preflightResult(),
                            execution.createResult());
                })
                .retryWhen(cleanupRetry())
                .then(Mono.<Void>fromRunnable(() -> lifecycle.descriptor().environments().forEach(environment -> {
                    var outcome = execution.outcomes().getOrDefault(
                            environment,
                            new CompletionOutcome(FunctionalTestState.CREATE_FAILED,
                                    FunctionalTestErrorSanitizer.sanitize(
                                            CREATE,
                                            new IllegalStateException())));
                    complete(
                            run,
                            new FunctionalTestKey(lifecycle.descriptor().systemId(), environment),
                            outcome.state(),
                            cleanupAttempts.get(),
                            outcome.error());
                })))
                .onErrorResume(throwable -> {
                    lifecycle.descriptor().environments().forEach(environment -> complete(
                            run,
                            new FunctionalTestKey(lifecycle.descriptor().systemId(), environment),
                            FunctionalTestState.CLEANUP_FAILED,
                            cleanupAttempts.get(),
                            FunctionalTestErrorSanitizer.sanitize(CLEANUP, throwable)));
                    return Mono.<Void>empty();
                });
    }

    private <P, C> PdlExecution<P, C> failedPdlExecution(
            PdlTestLifecycle<P, C> lifecycle,
            FunctionalTestState state,
            FunctionalTestError error
    ) {
        return new PdlExecution<>(
                Optional.empty(),
                Optional.empty(),
                failedPdlOutcomes(lifecycle, state, error));
    }

    private Map<no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment, CompletionOutcome>
    failedPdlOutcomes(
            PdlTestLifecycle<?, ?> lifecycle,
            FunctionalTestState state,
            FunctionalTestError error
    ) {
        var outcomes = new LinkedHashMap<
                no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment,
                CompletionOutcome>();
        lifecycle.descriptor().environments()
                .forEach(environment -> outcomes.put(environment, new CompletionOutcome(state, error)));
        return outcomes;
    }

    private void updatePdl(
            ActiveRun run,
            PdlTestLifecycle<?, ?> lifecycle,
            FunctionalTestState state,
            FunctionalTestError error
    ) {
        lifecycle.descriptor().environments().forEach(environment -> update(
                run,
                new FunctionalTestKey(lifecycle.descriptor().systemId(), environment),
                state,
                0,
                error));
    }

    private FunctionalTestContext pdlContext(PdlTestLifecycle<?, ?> lifecycle, ActiveRun run) {
        return new FunctionalTestContext(
                run.runId(),
                lifecycle.descriptor().systemId(),
                no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment.GLOBAL,
                run.startedAt());
    }

    private Mono<Void> execute(
            FunctionalTestRegistry.RegisteredFunctionalTest registration,
            ActiveRun run
    ) {
        return executeDefinition(registration.definition(), registration, run);
    }

    private <P, C, V> Mono<Void> executeDefinition(
            FunctionalTestDefinition<P, C, V> definition,
            FunctionalTestRegistry.RegisteredFunctionalTest registration,
            ActiveRun run
    ) {
        var context = new FunctionalTestContext(
                run.runId(),
                registration.key().systemId(),
                registration.environment(),
                run.startedAt());
        update(run, registration.key(), FunctionalTestState.PREFLIGHT, 0, null);

        return Mono.defer(() -> definition.preflight(context))
                .switchIfEmpty(Mono.error(new IllegalStateException("Preflight returned no result.")))
                .flatMap(preflightResult ->
                        executeCreate(definition, registration.key(), context, preflightResult, run))
                .onErrorResume(throwable -> {
                    var state = throwable instanceof FunctionalTestBlockedException
                            ? FunctionalTestState.BLOCKED
                            : FunctionalTestState.PREFLIGHT_FAILED;
                    return completeFailure(run, registration.key(), state, 0,
                            FunctionalTestErrorSanitizer.sanitize(PREFLIGHT, throwable));
                });
    }

    private <P, C, V> Mono<Void> executeCreate(
            FunctionalTestDefinition<P, C, V> definition,
            FunctionalTestKey key,
            FunctionalTestContext context,
            P preflightResult,
            ActiveRun run
    ) {
        update(run, key, FunctionalTestState.CREATE, 0, null);

        return Mono.defer(() -> definition.create(context, preflightResult))
                .switchIfEmpty(Mono.error(new IllegalStateException("Create returned no result.")))
                .flatMap(createResult ->
                        executeVerify(definition, key, context, preflightResult, createResult, run))
                .onErrorResume(throwable -> {
                    var error = FunctionalTestErrorSanitizer.sanitize(CREATE, throwable);
                    update(run, key, FunctionalTestState.CREATE_FAILED, 0, error);
                    return executeCleanup(
                            definition,
                            key,
                            context,
                            preflightResult,
                            Optional.empty(),
                            Optional.empty(),
                            new CompletionOutcome(FunctionalTestState.CREATE_FAILED, error),
                            run);
                });
    }

    private <P, C, V> Mono<Void> executeVerify(
            FunctionalTestDefinition<P, C, V> definition,
            FunctionalTestKey key,
            FunctionalTestContext context,
            P preflightResult,
            C createResult,
            ActiveRun run
    ) {
        update(run, key, FunctionalTestState.VERIFY, 0, null);

        return Mono.defer(() -> definition.verify(context, preflightResult, createResult))
                .switchIfEmpty(Mono.error(new IllegalStateException("Verify returned no result.")))
                .flatMap(verificationResult -> executeCleanup(
                        definition,
                        key,
                        context,
                        preflightResult,
                        Optional.of(createResult),
                        Optional.of(verificationResult),
                        new CompletionOutcome(FunctionalTestState.OK, null),
                        run))
                .onErrorResume(throwable -> {
                    var state = throwable instanceof FunctionalTestVerificationTimeoutException
                            || throwable instanceof java.util.concurrent.TimeoutException
                            ? FunctionalTestState.VERIFY_TIMEOUT
                            : FunctionalTestState.VERIFY_FAILED;
                    var error = FunctionalTestErrorSanitizer.sanitize(VERIFY, throwable);
                    update(run, key, state, 0, error);
                    return executeCleanup(
                            definition,
                            key,
                            context,
                            preflightResult,
                            Optional.of(createResult),
                            Optional.empty(),
                            new CompletionOutcome(state, error),
                            run);
                });
    }

    private <P, C, V> Mono<Void> executeCleanup(
            FunctionalTestDefinition<P, C, V> definition,
            FunctionalTestKey key,
            FunctionalTestContext context,
            P preflightResult,
            Optional<C> createResult,
            Optional<V> verificationResult,
            CompletionOutcome outcome,
            ActiveRun run
    ) {
        var cleanupAttempts = new AtomicInteger();

        return Mono.defer(() -> {
                    var attempt = cleanupAttempts.incrementAndGet();
                    update(run, key, FunctionalTestState.CLEANUP, attempt, outcome.error());
                    return definition.cleanup(
                            context,
                            preflightResult,
                            createResult,
                            verificationResult,
                            definition.descriptor().expectedCleanupState());
                })
                .retryWhen(Retry.backoff(3, Duration.ofMillis(250))
                        .maxBackoff(Duration.ofSeconds(2))
                        .filter(this::isRetryableCleanupFailure))
                .then(Mono.<Void>fromRunnable(() -> complete(
                        run,
                        key,
                        outcome.state(),
                        cleanupAttempts.get(),
                        outcome.error())))
                .onErrorResume(throwable -> completeFailure(
                        run,
                        key,
                        FunctionalTestState.CLEANUP_FAILED,
                        cleanupAttempts.get(),
                        FunctionalTestErrorSanitizer.sanitize(CLEANUP, throwable)));
    }

    private boolean isRetryableCleanupFailure(Throwable throwable) {
        if (throwable instanceof TimeoutException || throwable instanceof WebClientRequestException) {
            return true;
        }
        if (throwable instanceof WebClientResponseException responseException) {
            return responseException.getStatusCode().value() == 408
                    || responseException.getStatusCode().value() == 429
                    || responseException.getStatusCode().is5xxServerError();
        }
        return false;
    }

    private Retry cleanupRetry() {
        return Retry.backoff(3, Duration.ofMillis(250))
                .maxBackoff(Duration.ofSeconds(2))
                .filter(this::isRetryableCleanupFailure);
    }

    private Mono<Void> completeFailure(
            ActiveRun run,
            FunctionalTestKey key,
            FunctionalTestState state,
            int cleanupAttempts,
            FunctionalTestError error
    ) {
        return Mono.<Void>fromRunnable(() -> complete(run, key, state, cleanupAttempts, error));
    }

    private void update(
            ActiveRun run,
            FunctionalTestKey key,
            FunctionalTestState state,
            int cleanupAttempts,
            FunctionalTestError error
    ) {
        cache.update(key, run.runId(), state, cleanupAttempts, error)
                .ifPresent(run::update);
    }

    private void complete(
            ActiveRun run,
            FunctionalTestKey key,
            FunctionalTestState state,
            int cleanupAttempts,
            FunctionalTestError error
    ) {
        cache.complete(key, run.runId(), state, cleanupAttempts, error, clock.instant())
                .ifPresent(status -> recordCompletion(run, status));
    }

    private void recordCompletion(ActiveRun run, FunctionalTestStatus status) {
        run.update(status);
        resultListeners.forEach(listener -> {
            try {
                listener.onCompleted(status);
            } catch (RuntimeException exception) {
                log.error(
                        "Klarte ikke å behandle ferdig teststatus: {}",
                        exception.getClass().getSimpleName());
            }
        });
    }

    private void completeRun(ActiveRun run) {
        run.complete(clock.instant());
        synchronized (runMonitor) {
            if (activeRun == run) {
                activeRun = null;
            }
        }
    }

    private void failRun(ActiveRun run, Throwable throwable) {
        log.error("Uventet feil i funksjonstestkjøring: {}", throwable.getClass().getSimpleName());
        run.incompleteStatuses().forEach(status -> complete(
                run,
                new FunctionalTestKey(status.systemId(), status.environment()),
                FunctionalTestState.CREATE_FAILED,
                status.cleanupAttempts(),
                FunctionalTestErrorSanitizer.sanitize(CREATE, throwable)));
        completeRun(run);
    }

    private record CompletionOutcome(FunctionalTestState state, FunctionalTestError error) {
    }

    private record PdlExecution<P, C>(
            Optional<P> preflightResult,
            Optional<C> createResult,
            Map<FunctionalTestEnvironment, CompletionOutcome> outcomes
    ) {

        private boolean isReadyFor(FunctionalTestEnvironment environment) {
            if (environment == FunctionalTestEnvironment.GLOBAL) {
                return !outcomes.isEmpty()
                        && outcomes.values().stream()
                        .allMatch(outcome -> outcome.state() == FunctionalTestState.OK);
            }
            return Optional.ofNullable(outcomes.get(environment))
                    .map(CompletionOutcome::state)
                    .filter(FunctionalTestState.OK::equals)
                    .isPresent();
        }
    }

    private static final class ActiveRun {

        private final RunId runId;
        private final Instant startedAt;
        private final List<FunctionalTestKey> orderedKeys;
        private final Map<FunctionalTestKey, FunctionalTestStatus> statuses = new LinkedHashMap<>();
        private FunctionalTestRunState state = FunctionalTestRunState.RUNNING;
        private Instant completedAt;

        private ActiveRun(
                RunId runId,
                Instant startedAt,
                List<FunctionalTestKey> orderedKeys
        ) {
            this.runId = runId;
            this.startedAt = startedAt;
            this.orderedKeys = List.copyOf(orderedKeys);
        }

        private RunId runId() {
            return runId;
        }

        private Instant startedAt() {
            return startedAt;
        }

        private synchronized void update(FunctionalTestStatus status) {
            statuses.put(new FunctionalTestKey(status.systemId(), status.environment()), status);
        }

        private synchronized void complete(Instant completionTime) {
            state = FunctionalTestRunState.COMPLETED;
            completedAt = completionTime;
        }

        private synchronized List<FunctionalTestStatus> incompleteStatuses() {
            return statuses.values().stream()
                    .filter(status -> !status.state().isTerminal())
                    .toList();
        }

        private synchronized FunctionalTestRunStatus snapshot() {
            var orderedStatuses = new ArrayList<FunctionalTestStatus>();
            orderedKeys.forEach(key -> Optional.ofNullable(statuses.get(key)).ifPresent(orderedStatuses::add));
            return new FunctionalTestRunStatus(
                    runId,
                    state,
                    startedAt,
                    completedAt,
                    orderedStatuses);
        }
    }
}
