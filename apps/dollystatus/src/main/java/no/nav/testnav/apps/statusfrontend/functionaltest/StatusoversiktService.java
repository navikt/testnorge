package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Problem;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Status;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestKey;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class StatusoversiktService {

    private static final String TECHNICAL_STATUS_DOWN_MESSAGE = "Teknisk helsesjekk svarer ikke.";

    private final FunctionalTestCoordinator coordinator;
    private final FunctionalTestCache cache;
    private final Clock clock;
    private final Duration utdatertEtter;
    private final Instant oppstartet;

    public StatusoversiktService(
            FunctionalTestCoordinator coordinator,
            FunctionalTestCache cache,
            Clock clock,
            @Value("${dollystatus.utdatert-etter:PT6H}") Duration utdatertEtter
    ) {
        this.coordinator = coordinator;
        this.cache = cache;
        this.clock = clock;
        this.utdatertEtter = utdatertEtter;
        this.oppstartet = clock.instant();
    }

    public Mono<StatusoversiktResponse> getStatusoversikt() {
        return coordinator.getSystemStatuses()
                .flatMap(statuses -> {
                    var now = clock.instant();
                    var kjoringPagaar = coordinator.isRunActive();
                    var effectiveResults = statuses.stream()
                            .map(this::effectiveResult)
                            .flatMap(Optional::stream)
                            .toList();
                    var problems = effectiveResults.stream()
                            .filter(FunctionalTestStatus::hasProblem)
                            .map(this::toProblem)
                            .sorted(Comparator.comparing(Problem::navn).thenComparing(Problem::miljo))
                            .toList();

                    return nextManualRun(problems, kjoringPagaar, now)
                            .map(nesteManuelleKjoring -> summarize(
                                    statuses,
                                    effectiveResults,
                                    problems,
                                    kjoringPagaar,
                                    now,
                                    nesteManuelleKjoring.orElse(null)));
                });
    }

    private StatusoversiktResponse summarize(
            List<FunctionalTestStatus> statuses,
            List<FunctionalTestStatus> effectiveResults,
            List<Problem> problemer,
            boolean kjoringPagaar,
            Instant now,
            Instant nesteManuelleKjoringTidligst
    ) {
        var freshResults = effectiveResults.stream()
                .filter(status -> nonNull(status.completedAt())
                        && !status.completedAt().isBefore(now.minus(utdatertEtter)))
                .toList();
        var sistOppdatert = effectiveResults.stream()
                .map(FunctionalTestStatus::completedAt)
                .filter(value -> nonNull(value))
                .max(Comparator.naturalOrder())
                .orElse(null);

        return new StatusoversiktResponse(
                determineStatus(statuses.size(), freshResults),
                kjoringPagaar,
                sistOppdatert,
                nextAutomaticRun(statuses, kjoringPagaar),
                nesteManuelleKjoringTidligst,
                problemer);
    }

    private Optional<FunctionalTestStatus> effectiveResult(FunctionalTestStatus status) {
        if (status.state().isTerminal() && nonNull(status.completedAt())) {
            return Optional.of(status);
        }
        return cache.lastCompleted(new FunctionalTestKey(status.systemId(), status.environment()));
    }

    private Status determineStatus(int statusCount, List<FunctionalTestStatus> freshResults) {
        if (freshResults.stream().anyMatch(FunctionalTestStatus::isFailure)) {
            return Status.FEIL;
        }
        if (freshResults.stream().anyMatch(FunctionalTestStatus::hasProblem)) {
            return Status.ADVARSEL;
        }
        if (statusCount == 0 || freshResults.size() < statusCount) {
            return Status.UKJENT;
        }
        return Status.OK;
    }

    private Instant nextAutomaticRun(List<FunctionalTestStatus> statuses, boolean kjoringPagaar) {
        if (kjoringPagaar) {
            return null;
        }
        if (statuses.stream().anyMatch(status -> isNull(status.completedAt()))) {
            return oppstartet;
        }
        return statuses.stream()
                .map(FunctionalTestStatus::cachedUntil)
                .filter(value -> nonNull(value))
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private Mono<Optional<Instant>> nextManualRun(
            List<Problem> problemer,
            boolean kjoringPagaar,
            Instant now
    ) {
        if (kjoringPagaar || problemer.isEmpty()) {
            return Mono.just(Optional.empty());
        }

        return coordinator.nesteManuelleKjoringForFeilede()
                .map(cooldown -> Optional.of(cooldown
                        .filter(value -> value.isAfter(now))
                        .orElse(now)));
    }

    private Problem toProblem(FunctionalTestStatus status) {
        return new Problem(
                status.systemId().value(),
                status.displayName().value(),
                status.environment().name(),
                status.state().name(),
                message(status),
                nonNull(status.completedAt()) ? status.completedAt() : status.startedAt());
    }

    private String message(FunctionalTestStatus status) {
        if (nonNull(status.error())) {
            return status.error().message();
        }
        if (status.state() == FunctionalTestState.TECHNICAL_ONLY
                && status.technicalStatus().state() == TechnicalStatusState.DOWN) {
            return TECHNICAL_STATUS_DOWN_MESSAGE;
        }
        return null;
    }
}
