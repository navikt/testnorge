package no.nav.testnav.apps.statusfrontend.slack;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SlackTransitionTrackerTest {

    private static final Instant COMPLETED_AT = Instant.parse("2026-09-21T10:00:00Z");

    @Test
    void shouldNotifyOnlyWhenStatusTurnsRedAndRearmSilentlyAfterSuccess() {
        var tracker = new SlackTransitionTracker();

        assertThat(tracker.update(status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UNKNOWN)))
                .contains(SlackTransitionTracker.SlackTransition.RED);
        assertThat(tracker.update(status(FunctionalTestState.CLEANUP_FAILED, TechnicalStatusState.UNKNOWN)))
                .isEmpty();
        assertThat(tracker.update(status(FunctionalTestState.OK, TechnicalStatusState.UNKNOWN)))
                .isEmpty();
        assertThat(tracker.update(status(FunctionalTestState.OK, TechnicalStatusState.UNKNOWN)))
                .isEmpty();
        assertThat(tracker.update(status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UNKNOWN)))
                .contains(SlackTransitionTracker.SlackTransition.RED);
    }

    @Test
    void shouldTreatFailedTechnicalStatusAsRed() {
        var tracker = new SlackTransitionTracker();

        assertThat(tracker.update(status(
                        FunctionalTestState.TECHNICAL_ONLY,
                        TechnicalStatusState.DOWN)))
                .contains(SlackTransitionTracker.SlackTransition.RED);
        assertThat(tracker.update(status(
                        FunctionalTestState.TECHNICAL_ONLY,
                        TechnicalStatusState.UP)))
                .isEmpty();
        assertThat(tracker.update(status(
                        FunctionalTestState.TECHNICAL_ONLY,
                        TechnicalStatusState.DOWN)))
                .contains(SlackTransitionTracker.SlackTransition.RED);
    }

    @ParameterizedTest
    @EnumSource(value = FunctionalTestState.class,
            names = {"BLOCKED", "NOT_RUN", "RUNNING", "PREFLIGHT", "CREATE", "VERIFY", "CLEANUP"})
    void shouldIgnoreSkippedAndRunningTestsWithoutRearmingAlerts(FunctionalTestState state) {
        var tracker = new SlackTransitionTracker();

        assertThat(tracker.update(status(state, TechnicalStatusState.UNKNOWN))).isEmpty();
        assertThat(tracker.update(status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UNKNOWN)))
                .contains(SlackTransitionTracker.SlackTransition.RED);
        assertThat(tracker.update(status(state, TechnicalStatusState.UNKNOWN))).isEmpty();
        assertThat(tracker.update(status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UNKNOWN)))
                .isEmpty();
    }

    @Test
    void shouldIgnoreUnknownTechnicalStatus() {
        var tracker = new SlackTransitionTracker();

        assertThat(tracker.update(status(FunctionalTestState.TECHNICAL_ONLY, TechnicalStatusState.UNKNOWN)))
                .isEmpty();
    }

    private static FunctionalTestStatus status(
            FunctionalTestState state,
            TechnicalStatusState technicalState
    ) {
        return new FunctionalTestStatus(
                new SystemId("aareg"),
                new DisplayName("Arbeidsregisteret (AAREG)"),
                FunctionalTestEnvironment.GLOBAL,
                RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d"),
                state,
                COMPLETED_AT.minusSeconds(30),
                COMPLETED_AT,
                COMPLETED_AT.plusSeconds(3600),
                0,
                null,
                new TechnicalStatus(technicalState, COMPLETED_AT));
    }
}
