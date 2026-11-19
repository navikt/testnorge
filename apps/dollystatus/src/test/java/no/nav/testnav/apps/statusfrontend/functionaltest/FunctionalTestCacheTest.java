package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalTestCacheTest {

    private static final FunctionalTestKey KEY =
            new FunctionalTestKey(new SystemId("arena"), FunctionalTestEnvironment.Q1);
    private static final DisplayName DISPLAY_NAME = new DisplayName("Arena");

    @Test
    void shouldCacheSuccessfulResultUntilSixHoursAfterCompletion() {
        var startedAt = Instant.parse("2026-09-21T10:00:00Z");
        var completedAt = startedAt.plusSeconds(30);
        var clock = new MutableClock(completedAt);
        var cache = new FunctionalTestCache(clock);
        var runId = RunId.random();

        cache.start(KEY, DISPLAY_NAME, runId, startedAt);
        var completedStatus = cache.complete(
                KEY,
                runId,
                FunctionalTestState.OK,
                1,
                null,
                completedAt);

        assertThat(completedStatus).get()
                .extracting(FunctionalTestStatus::cachedUntil)
                .isEqualTo(completedAt.plus(Duration.ofHours(6)));
        assertThat(cache.isExpired(KEY)).isFalse();

        clock.setInstant(completedAt.plus(Duration.ofHours(6)));

        assertThat(cache.isExpired(KEY)).isTrue();
    }

    @Test
    void shouldCacheFailedResultUntilOneHourAfterCompletion() {
        var startedAt = Instant.parse("2026-09-21T10:00:00Z");
        var completedAt = startedAt.plusSeconds(30);
        var cache = new FunctionalTestCache(new MutableClock(completedAt));
        var runId = RunId.random();

        cache.start(KEY, DISPLAY_NAME, runId, startedAt);
        var completedStatus = cache.complete(
                KEY,
                runId,
                FunctionalTestState.VERIFY_FAILED,
                1,
                null,
                completedAt);

        assertThat(completedStatus).get()
                .extracting(FunctionalTestStatus::cachedUntil)
                .isEqualTo(completedAt.plus(Duration.ofHours(1)));
    }

    @Test
    void shouldCacheSuccessfulTechnicalStatusUntilSixHoursAfterCompletion() {
        var startedAt = Instant.parse("2026-09-21T10:00:00Z");
        var completedAt = startedAt.plusSeconds(30);
        var cache = new FunctionalTestCache(new MutableClock(completedAt));
        var runId = RunId.random();

        cache.start(KEY, DISPLAY_NAME, runId, startedAt);
        var completedStatus = cache.completeTechnical(KEY, runId, TechnicalStatusState.UP, completedAt);

        assertThat(completedStatus).get()
                .extracting(FunctionalTestStatus::cachedUntil)
                .isEqualTo(completedAt.plus(Duration.ofHours(6)));
    }

    @Test
    void shouldCacheFailedTechnicalStatusUntilOneHourAfterCompletion() {
        var startedAt = Instant.parse("2026-09-21T10:00:00Z");
        var completedAt = startedAt.plusSeconds(30);
        var cache = new FunctionalTestCache(new MutableClock(completedAt));
        var runId = RunId.random();

        cache.start(KEY, DISPLAY_NAME, runId, startedAt);
        var completedStatus = cache.completeTechnical(KEY, runId, TechnicalStatusState.DOWN, completedAt);

        assertThat(completedStatus).get()
                .extracting(FunctionalTestStatus::cachedUntil)
                .isEqualTo(completedAt.plus(Duration.ofHours(1)));
    }

    @Test
    void shouldUseCustomCacheDurations() {
        var startedAt = Instant.parse("2026-09-21T10:00:00Z");
        var completedAt = startedAt.plusSeconds(30);
        var cache = new FunctionalTestCache(
                new MutableClock(completedAt),
                Duration.ofHours(2),
                Duration.ofMinutes(15));
        var runId = RunId.random();

        cache.start(KEY, DISPLAY_NAME, runId, startedAt);
        var successfulStatus = cache.complete(
                KEY,
                runId,
                FunctionalTestState.OK,
                1,
                null,
                completedAt);

        assertThat(successfulStatus).get()
                .extracting(FunctionalTestStatus::cachedUntil)
                .isEqualTo(completedAt.plus(Duration.ofHours(2)));

        var failedRunId = RunId.random();
        var failedStartedAt = completedAt.plusSeconds(1);
        var failedCompletedAt = failedStartedAt.plusSeconds(30);
        cache.start(KEY, DISPLAY_NAME, failedRunId, failedStartedAt);
        var failedStatus = cache.complete(
                KEY,
                failedRunId,
                FunctionalTestState.VERIFY_FAILED,
                1,
                null,
                failedCompletedAt);

        assertThat(failedStatus).get()
                .extracting(FunctionalTestStatus::cachedUntil)
                .isEqualTo(failedCompletedAt.plus(Duration.ofMinutes(15)));
    }

    @Test
    void shouldApplyCooldownForFiveMinutesFromStart() {
        var startedAt = Instant.parse("2026-09-21T10:00:00Z");
        var clock = new MutableClock(startedAt.plus(Duration.ofMinutes(4)));
        var cache = new FunctionalTestCache(clock);

        cache.start(KEY, DISPLAY_NAME, RunId.random(), startedAt);

        assertThat(cache.cooldownUntil(KEY))
                .contains(startedAt.plus(Duration.ofMinutes(5)));

        clock.setInstant(startedAt.plus(Duration.ofMinutes(5)));

        assertThat(cache.cooldownUntil(KEY)).isEmpty();
    }
}
