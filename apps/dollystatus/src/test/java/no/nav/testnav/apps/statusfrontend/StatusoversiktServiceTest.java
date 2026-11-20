package no.nav.testnav.apps.statusfrontend;

import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Problem;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Status;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCache;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCoordinator;
import no.nav.testnav.apps.statusfrontend.functionaltest.StatusoversiktService;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatusoversiktServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-18T12:00:00Z");
    private static final Instant COMPLETED_AT = NOW.minus(Duration.ofHours(1));
    private static final Instant CACHED_UNTIL = NOW.plus(Duration.ofHours(5));

    @Mock
    private FunctionalTestCoordinator coordinator;

    @Mock
    private FunctionalTestCache cache;

    private StatusoversiktService service;

    @BeforeEach
    void setUp() {
        service = new StatusoversiktService(
                coordinator, cache, Clock.fixed(NOW, ZoneOffset.UTC), Duration.ofHours(6));
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
        givenStatusesWithManualRun(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.VERIFY_FAILED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown())),
                false,
                Optional.empty());

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.status()).isEqualTo(Status.FEIL))
                .verifyComplete();
    }

    @Test
    void shouldReturnWarningForBlockedCleanupFailedAndTechnicalDownStatuses() {
        givenStatusesWithManualRun(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.CLEANUP_FAILED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("sigrun", "Sigrun", FunctionalTestEnvironment.GLOBAL, FunctionalTestState.TECHNICAL_ONLY,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null,
                        new TechnicalStatus(TechnicalStatusState.DOWN, COMPLETED_AT))),
                false,
                Optional.empty());

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
    void shouldReturnUnknownForStaleFailureAndKeepItInProblems() {
        var staleCompletion = NOW.minus(Duration.ofHours(7));
        givenStatusesWithManualRun(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.VERIFY_FAILED,
                staleCompletion.minusSeconds(30),
                staleCompletion,
                staleCompletion,
                null,
                TechnicalStatus.unknown())), false, Optional.empty());

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.status()).isEqualTo(Status.UKJENT);
                    assertThat(response.problemer()).extracting(Problem::navn).containsExactly("Arena");
                    assertThat(response.sistOppdatert()).isEqualTo(staleCompletion);
                })
                .verifyComplete();
    }

    @Test
    void shouldReturnUnknownWhenAnotherStatusHasNotRun() {
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.OK,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.NOT_RUN,
                        null, null, null, null, TechnicalStatus.unknown())), false);

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.status()).isEqualTo(Status.UKJENT))
                .verifyComplete();
    }

    @Test
    void shouldKeepLastCompletedFailureDuringRerun() {
        var runningStatus = status(
                "pdl",
                "PDL",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.RUNNING,
                NOW.minusSeconds(30),
                null,
                null,
                null,
                TechnicalStatus.unknown());
        var lastCompletedStatus = status(
                "pdl",
                "PDL",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.VERIFY_FAILED,
                COMPLETED_AT.minusSeconds(30),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown());
        givenStatuses(List.of(runningStatus), true);
        when(cache.lastCompleted(new FunctionalTestKey(
                SystemId.from("pdl"), FunctionalTestEnvironment.Q1)))
                .thenReturn(Optional.of(lastCompletedStatus));

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.status()).isEqualTo(Status.FEIL);
                    assertThat(response.problemer()).extracting(Problem::navn).containsExactly("PDL");
                    assertThat(response.sistOppdatert()).isEqualTo(COMPLETED_AT);
                })
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
    void shouldUseServiceStartAsStableNextAutomaticRunWhenNotRunStatusesExist() {
        var movingClock = mock(Clock.class);
        when(movingClock.instant()).thenReturn(NOW, NOW.plus(Duration.ofMinutes(3)));
        var movingService = new StatusoversiktService(coordinator, cache, movingClock, Duration.ofHours(6));
        givenStatuses(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.OK,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null, TechnicalStatus.unknown()),
                status("pdl", "PDL", FunctionalTestEnvironment.Q1, FunctionalTestState.NOT_RUN,
                        null, null, null, null, TechnicalStatus.unknown())), false);

        StepVerifier.create(movingService.getStatusoversikt())
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
    void shouldUseCoordinatorValueForNextManualRun() {
        var nextManualRun = NOW.plus(Duration.ofMinutes(3));
        givenStatusesWithManualRun(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.BLOCKED,
                NOW.minus(Duration.ofMinutes(2)),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false, Optional.of(nextManualRun));

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst())
                        .isEqualTo(nextManualRun))
                .verifyComplete();
    }

    @Test
    void shouldReturnNowWhenProblemCooldownHasPassed() {
        givenStatusesWithManualRun(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.BLOCKED,
                NOW.minus(Duration.ofMinutes(6)),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false, Optional.empty());

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst()).isEqualTo(NOW))
                .verifyComplete();
    }

    @Test
    void shouldUseFullRunCooldownAsNextManualRunWithoutProblems() {
        var nextFullRun = NOW.plus(Duration.ofMinutes(14));
        givenStatuses(List.of(status(
                "arena",
                "Arena",
                FunctionalTestEnvironment.Q1,
                FunctionalTestState.OK,
                NOW.minusSeconds(60),
                COMPLETED_AT,
                CACHED_UNTIL,
                null,
                TechnicalStatus.unknown())), false);
        when(coordinator.nesteManuelleKjoringForAlle()).thenReturn(Optional.of(nextFullRun));

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst()).isEqualTo(nextFullRun))
                .verifyComplete();

        when(coordinator.nesteManuelleKjoringForAlle()).thenReturn(Optional.empty());

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> assertThat(response.nesteManuelleKjoringTidligst()).isEqualTo(NOW))
                .verifyComplete();
    }

    @Test
    void shouldReturnNullAsNextManualRunWhileRunning() {
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
        givenStatusesWithManualRun(List.of(
                status("arena", "Arena", FunctionalTestEnvironment.Q1, FunctionalTestState.BLOCKED,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL,
                        new FunctionalTestError(FunctionalTestErrorCategory.DOWNSTREAM_SERVER, errorMessage),
                        TechnicalStatus.unknown()),
                status("sigrun", "Sigrun", FunctionalTestEnvironment.GLOBAL, FunctionalTestState.TECHNICAL_ONLY,
                        NOW.minusSeconds(30), COMPLETED_AT, CACHED_UNTIL, null,
                        new TechnicalStatus(TechnicalStatusState.DOWN, COMPLETED_AT))),
                false,
                Optional.empty());

        StepVerifier.create(service.getStatusoversikt())
                .assertNext(response -> {
                    assertThat(response.problemer()).extracting(Problem::melding)
                            .containsExactly(errorMessage, "Teknisk helsesjekk svarer ikke.");
                    assertThat(response.problemer().getFirst().tidspunkt()).isEqualTo(COMPLETED_AT);
                })
                .verifyComplete();
    }

    private void givenStatuses(List<FunctionalTestStatus> statuses, boolean runActive) {
        when(coordinator.getSystemStatuses()).thenReturn(Mono.just(statuses));
        when(coordinator.isRunActive()).thenReturn(runActive);
    }

    private void givenStatusesWithManualRun(
            List<FunctionalTestStatus> statuses,
            boolean runActive,
            Optional<Instant> nextManualRun
    ) {
        givenStatuses(statuses, runActive);
        when(coordinator.nesteManuelleKjoringForFeilede()).thenReturn(Mono.just(nextManualRun));
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
