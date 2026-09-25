package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalTestCacheTest {

    private static final FunctionalTestKey KEY =
            new FunctionalTestKey(new SystemId("arena"), FunctionalTestEnvironment.Q1);
    private static final DisplayName DISPLAY_NAME = new DisplayName("Arena");

    @Test
    void shouldCacheResultUntilOneHourAfterCompletion() {
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
                .extracting(status -> status.cachedUntil())
                .isEqualTo(completedAt.plus(Duration.ofHours(1)));
        assertThat(cache.isExpired(KEY)).isFalse();

        clock.setInstant(completedAt.plus(Duration.ofHours(1)));

        assertThat(cache.isExpired(KEY)).isTrue();
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
