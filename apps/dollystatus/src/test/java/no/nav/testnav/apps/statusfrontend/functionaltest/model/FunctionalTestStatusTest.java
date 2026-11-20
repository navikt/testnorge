package no.nav.testnav.apps.statusfrontend.functionaltest.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FunctionalTestStatusTest {

    @Test
    void shouldReportProblemForFailedBlockedAndDownTechnicalStatuses() {
        assertThat(status(FunctionalTestState.PREFLIGHT_FAILED, TechnicalStatusState.UP).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UP).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.VERIFY_FAILED, TechnicalStatusState.UP).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.VERIFY_TIMEOUT, TechnicalStatusState.UP).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.CLEANUP_FAILED, TechnicalStatusState.UP).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.BLOCKED, TechnicalStatusState.UP).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.TECHNICAL_ONLY, TechnicalStatusState.DOWN).hasProblem()).isTrue();
        assertThat(status(FunctionalTestState.TECHNICAL_ONLY, TechnicalStatusState.UP).hasProblem()).isFalse();
    }

    @Test
    void shouldReportFailureOnlyForFunctionalTestFailures() {
        assertThat(status(FunctionalTestState.PREFLIGHT_FAILED, TechnicalStatusState.UP).isFailure()).isTrue();
        assertThat(status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UP).isFailure()).isTrue();
        assertThat(status(FunctionalTestState.VERIFY_FAILED, TechnicalStatusState.UP).isFailure()).isTrue();
        assertThat(status(FunctionalTestState.VERIFY_TIMEOUT, TechnicalStatusState.UP).isFailure()).isTrue();
        assertThat(status(FunctionalTestState.CLEANUP_FAILED, TechnicalStatusState.UP).isFailure()).isFalse();
        assertThat(status(FunctionalTestState.BLOCKED, TechnicalStatusState.UP).isFailure()).isFalse();
        assertThat(status(FunctionalTestState.TECHNICAL_ONLY, TechnicalStatusState.DOWN).isFailure()).isFalse();
    }

    private static FunctionalTestStatus status(FunctionalTestState state, TechnicalStatusState technicalStatusState) {
        return new FunctionalTestStatus(
                new SystemId("arena"),
                new DisplayName("Arena"),
                FunctionalTestEnvironment.Q1,
                RunId.random(),
                state,
                null,
                null,
                null,
                0,
                null,
                new TechnicalStatus(technicalStatusState, null));
    }
}
