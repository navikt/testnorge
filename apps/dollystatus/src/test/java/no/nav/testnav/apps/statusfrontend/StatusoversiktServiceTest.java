package no.nav.testnav.apps.statusfrontend;

import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Problem;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Status;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCoordinator;
import no.nav.testnav.apps.statusfrontend.functionaltest.StatusoversiktService;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusoversiktServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-18T12:00:00Z");
    private static final Instant COMPLETED_AT = NOW.minus(Duration.ofHours(1));
    private static final Instant CACHED_UNTIL = NOW.plus(Duration.ofHours(5));

    @Mock
    private FunctionalTestCoordinator coordinator;

    private StatusoversiktService service;

    @BeforeEach
    void setUp() {
        service = new StatusoversiktService(coordinator, Clock.fixed(NOW, ZoneOffset.UTC), Duration.ofHours(6));
    }

    @Test
    void shouldReturnOkWhenStatusesAreFreshAndHaveNoProblems() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.OK,
                NOW.minus(Duration.ofHours(2)),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.status()).isEqualTo(Status.OK);
                    assertThat(response.sistOppdatert()).isEqualTo(COMPLETED_AT);
                })
                .verifyComplete();
    }

    @Test
    void shouldPrioritizeFailureOverWarning() {
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.VERIFY_FAILED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.status()).isEqualTo(Status.FEIL))
                .verifyComplete();
    }

    @Test
    void shouldReturnWarningForBlockedCleanupFailedAndTechnicalDownStatuses() {
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.CLEANUP_FAILED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("sigrun", "Sigrun", FunctionalTestEnvironment.GLOBAL, FunctionalTestState.TECHNICAL_ONLY,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null,
                        new TechnicalStatus(TechnicalStatusState.DOWN, COMPLETED_AT))), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.status()).isEqualTo(Status.ADVARSEL);
                    assertThat(response.problemer()).extracting(Problem::navn)
                            .containsExactly("Arena", "PDL", "Sigrun");
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnUnknownWhenNothingHasCompleted() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.NOT_RUN,
                null,
                null,
                null,
                null,
                TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.status()).isEqualTo(Status.UKJENT);
                    assertThat(response.sistOppdatert()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnUnknownWhenLatestCompletionIsOlderThanSixHours() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.OK,
                NOW.minus(Duration.ofHours(7)),
                NOW.minus(Duration.ofHours(6)).minusSeconds(1),
                NOW.plusSeconds(1),
                null,
                TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.status()).isEqualTo(Status.UKJENT))
                .verifyComplete();
    }

    @Test
    void shouldReturnOkWhenLatestCompletionIsWithinSixHours() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.OK,
                NOW.minus(Duration.ofHours(6)),
                NOW.minus(Duration.ofHours(6)),
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.status()).isEqualTo(Status.OK))
                .verifyComplete();
    }

    @Test
    void shouldUseNowAsNextAutomaticRunWhenNotRunStatusesExist() {
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.OK,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.NOT_RUN,
                        null, null, null, null, TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteAutomatiskeKjoring()).isEqualTo(NOW))
                .verifyComplete();
    }

    @Test
    void shouldUseEarliestCacheExpiryAsNextAutomaticRun() {
        var earlierCacheExpiry = NOW.plus(Duration.ofHours(2));
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.OK,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.OK,
                        NOW.minusSeconds(30), COMPLETED_AT, earlierCacheExpiry, null, TechnicalStatus.unknown())),
                false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteAutomatiskeKjoring()).isEqualTo(earlierCacheExpiry))
                .verifyComplete();
    }

    @Test
    void shouldReturnNullAsNextAutomaticRunWhileRunning() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.NOT_RUN,
                null,
                null,
                null,
                null,
                TechnicalStatus.unknown())), true);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.kjoringPagaar()).isTrue();
                    assertThat(response.nesteAutomatiskeKjoring()).isNull();
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnLatestProblemSystemCooldownAcrossEnvironments() {
        var laterStartedAt = NOW.minusSeconds(30);
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minus(Duration.ofMinutes(2)), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("arena", "Arena", FunctionalTestEnvironment.Q2, FunctionalTestState.OK,
                        laterStartedAt, COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst())
                        .isEqualTo(laterStartedAt.plus(Duration.ofMinutes(5))))
                .verifyComplete();
    }

    @Test
    void shouldReturnNowWhenProblemCooldownHasPassed() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.BLOCKED,
                NOW.minus(Duration.ofMinutes(6)),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst()).isEqualTo(NOW))
                .verifyComplete();
    }

    @Test
    void shouldReturnNullAsNextManualRunWithoutProblemsOrWhileRunning() {
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.OK,
                NOW.minusSeconds(30),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst()).isNull())
                .verifyComplete();

        givenStatuses(List.of(status(
                "pdl",
                "PDL",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.BLOCKED,
                NOW.minusSeconds(30),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), true);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst()).isNull())
                .verifyComplete();
    }

    @Test
    void shouldExposeSanitizedErrorAndTechnicalDownMessageForProblems() {
        var errorMessage = "The downstream system returned an error.";
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minusSeconds(30), null, CACHED_UNTIL,
                        new FunctionalTestError(FunctionalTestErrorCategory.DOWNSTREAM_SERVER, errorMessage),
                        TechnicalStatus.unknown()),
                status("sigrun", "Sigrun", FunctionalTestEnvironment.GLOBAL, FunctionalTestState.TECHNICAL_ONLY,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null,
                        new TechnicalStatus(TechnicalStatusState.DOWN, COMPLETED_AT))), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.problemer()).extracting(Problem::melding)
                            .containsExactly(errorMessage, "Teknisk helsesjekk svarer ikke.");
                    assertThat(response.problemer().getFirst().tidspunkt()).isEqualTo(NOW.minusSeconds(30));
                })
                .verifyComplete();
    }

    private void givenStatuses(List<FunctionalTestStatus> statuses, boolean runActive) {
        when(coordinator.getSystemStatuses()).thenReturn(Mono.just(statuses));
        when(coordinator.isRunActive()).thenReturn(runActive);
    }

    private static FunctionalTestStatus status(
            String systemId,
            String name,
            FunctionalTestEnvironment environment,
            FunctionalTestState state,
            Instant startedAt,
            Instant completedAt,
            Instant cachedUntil,
            FunctionalTestError error,
            TechnicalStatus technicalStatus
    ) {
        return new FunctionalTestStatus(
                SystemId.from(systemId),
                new DisplayName(name),
                environment,
                null,
                state,
                startedAt,
                completedAt,
                cachedUntil,
                0,
                error,
                technicalStatus);
    }
}
