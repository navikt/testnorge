package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FunctionalTestCache {

    static final Duration CACHE_TTL = Duration.ofHours(1);
    static final Duration MANUAL_COOLDOWN = Duration.ofMinutes(5);

    private final Clock clock;
    private final Map<FunctionalTestKey, FunctionalTestStatus> statuses = new ConcurrentHashMap<>();

    public FunctionalTestCache(Clock clock) {
        this.clock = clock;
    }

    public Optional<FunctionalTestStatus> get(FunctionalTestKey key) {
        return Optional.ofNullable(statuses.get(key));
    }

    public FunctionalTestStatus statusFor(FunctionalTestRegistry.RegisteredFunctionalTest registration) {
        return statusFor(
                registration.key(),
                registration.definition().descriptor().displayName());
    }

    public FunctionalTestStatus statusFor(
            FunctionalTestDescriptor descriptor,
            FunctionalTestEnvironment environment
    ) {
        return statusFor(
                new FunctionalTestKey(descriptor.systemId(), environment),
                descriptor.displayName());
    }

    public FunctionalTestStatus statusFor(FunctionalTestRegistry.RegisteredTechnicalStatus registration) {
        return statusFor(
                registration.key(),
                registration.definition().descriptor().displayName());
    }

    private FunctionalTestStatus statusFor(FunctionalTestKey key, DisplayName displayName) {
        return get(key)
                .orElseGet(() -> FunctionalTestStatus.notRun(key, displayName, TechnicalStatus.unknown()));
    }

    public boolean isExpired(FunctionalTestKey key) {
        return get(key)
                .filter(status -> status.completedAt() != null)
                .filter(status -> status.cachedUntil() != null)
                .map(status -> !clock.instant().isBefore(status.cachedUntil()))
                .orElse(true);
    }

    public Optional<Instant> cooldownUntil(FunctionalTestKey key) {
        return get(key)
                .map(FunctionalTestStatus::startedAt)
                .map(startedAt -> startedAt.plus(MANUAL_COOLDOWN))
                .filter(cooldownUntil -> clock.instant().isBefore(cooldownUntil));
    }

    public synchronized FunctionalTestStatus start(
            FunctionalTestKey key,
            DisplayName displayName,
            RunId runId,
            Instant startedAt
    ) {
        var existingStatus = statuses.get(key);
        if (existingStatus != null && existingStatus.startedAt() != null
                && existingStatus.startedAt().isAfter(startedAt)) {
            return existingStatus;
        }

        var technicalStatus = existingStatus == null
                ? TechnicalStatus.unknown()
                : existingStatus.technicalStatus();
        var status = new FunctionalTestStatus(
                key.systemId(),
                displayName,
                key.environment(),
                runId,
                FunctionalTestState.RUNNING,
                startedAt,
                null,
                null,
                0,
                null,
                technicalStatus);
        statuses.put(key, status);
        return status;
    }

    public synchronized Optional<FunctionalTestStatus> update(
            FunctionalTestKey key,
            RunId runId,
            FunctionalTestState state,
            int cleanupAttempts,
            FunctionalTestError error
    ) {
        var existingStatus = statuses.get(key);
        if (existingStatus == null || !runId.equals(existingStatus.runId())) {
            return Optional.empty();
        }

        var updatedStatus = new FunctionalTestStatus(
                existingStatus.systemId(),
                existingStatus.displayName(),
                existingStatus.environment(),
                existingStatus.runId(),
                state,
                existingStatus.startedAt(),
                null,
                null,
                cleanupAttempts,
                error,
                existingStatus.technicalStatus());
        statuses.put(key, updatedStatus);
        return Optional.of(updatedStatus);
    }

    public synchronized Optional<FunctionalTestStatus> complete(
            FunctionalTestKey key,
            RunId runId,
            FunctionalTestState state,
            int cleanupAttempts,
            FunctionalTestError error,
            Instant completedAt
    ) {
        var existingStatus = statuses.get(key);
        if (existingStatus == null || !runId.equals(existingStatus.runId())) {
            return Optional.empty();
        }

        var completedStatus = new FunctionalTestStatus(
                existingStatus.systemId(),
                existingStatus.displayName(),
                existingStatus.environment(),
                existingStatus.runId(),
                state,
                existingStatus.startedAt(),
                completedAt,
                completedAt.plus(CACHE_TTL),
                cleanupAttempts,
                error,
                existingStatus.technicalStatus());
        statuses.put(key, completedStatus);
        return Optional.of(completedStatus);
    }

    public synchronized Optional<FunctionalTestStatus> completeTechnical(
            FunctionalTestKey key,
            RunId runId,
            TechnicalStatusState technicalStatusState,
            Instant completedAt
    ) {
        var existingStatus = statuses.get(key);
        if (existingStatus == null || !runId.equals(existingStatus.runId())) {
            return Optional.empty();
        }

        var completedStatus = new FunctionalTestStatus(
                existingStatus.systemId(),
                existingStatus.displayName(),
                existingStatus.environment(),
                existingStatus.runId(),
                FunctionalTestState.TECHNICAL_ONLY,
                existingStatus.startedAt(),
                completedAt,
                completedAt.plus(CACHE_TTL),
                0,
                null,
                new TechnicalStatus(technicalStatusState, completedAt));
        statuses.put(key, completedStatus);
        return Optional.of(completedStatus);
    }
}
