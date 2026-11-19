package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Problem;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse.Status;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class StatusoversiktService {

    private static final String TECHNICAL_STATUS_DOWN_MESSAGE = "Teknisk helsesjekk svarer ikke.";

    private final FunctionalTestCoordinator coordinator;
    private final Clock clock;
    private final Duration utdatertEtter;

    public StatusoversiktService(
            FunctionalTestCoordinator coordinator,
            Clock clock,
            @Value("${dollystatus.utdatert-etter:PT6H}") Duration utdatertEtter
    ) {
        this.coordinator = coordinator;
        this.clock = clock;
        this.utdatertEtter = utdatertEtter;
    }

    public Mono<StatusoversiktResponse> getStatusoversikt() {
        return coordinator.getSystemStatuses()
                .map(statuses -> summarize(statuses, coordinator.isRunActive(), clock.instant()));
    }

    private StatusoversiktResponse summarize(
            List<FunctionalTestStatus> statuses,
            boolean kjoringPagaar,
            Instant now
    ) {
        var sistOppdatert = statuses.stream()
                .map(FunctionalTestStatus::completedAt)
                .filter(value -> nonNull(value))
                .max(Comparator.naturalOrder())
                .orElse(null);
        var terminalStatuses = statuses.stream()
                .filter(status -> status.state().isTerminal())
                .toList();
        var problemer = statuses.stream()
                .filter(FunctionalTestStatus::hasProblem)
                .map(this::toProblem)
                .sorted(Comparator.comparing(Problem::navn).thenComparing(Problem::miljo))
                .toList();

        return new StatusoversiktResponse(
                determineStatus(terminalStatuses, sistOppdatert, now),
                kjoringPagaar,
                sistOppdatert,
                nextAutomaticRun(statuses, kjoringPagaar, now),
                nextManualRun(statuses, problemer, kjoringPagaar, now),
                problemer);
    }

    private Status determineStatus(List<FunctionalTestStatus> terminalStatuses, Instant sistOppdatert, Instant now) {
        if (terminalStatuses.stream().anyMatch(FunctionalTestStatus::isFailure)) {
            return Status.FEIL;
        }
        if (terminalStatuses.stream().anyMatch(FunctionalTestStatus::hasProblem)) {
            return Status.ADVARSEL;
        }
        if (isNull(sistOppdatert) || sistOppdatert.isBefore(now.minus(utdatertEtter))) {
            return Status.UKJENT;
        }
        return Status.OK;
    }

    private Instant nextAutomaticRun(List<FunctionalTestStatus> statuses, boolean kjoringPagaar, Instant now) {
        if (kjoringPagaar) {
            return null;
        }
        if (statuses.stream().anyMatch(status -> isNull(status.completedAt()))) {
            return now;
        }
        return statuses.stream()
                .map(FunctionalTestStatus::cachedUntil)
                .filter(value -> nonNull(value))
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    private Instant nextManualRun(
            List<FunctionalTestStatus> statuses,
            List<Problem> problemer,
            boolean kjoringPagaar,
            Instant now
    ) {
        if (kjoringPagaar || problemer.isEmpty()) {
            return null;
        }

        Set<SystemId> problemSystemIds = statuses.stream()
                .filter(FunctionalTestStatus::hasProblem)
                .map(FunctionalTestStatus::systemId)
                .collect(Collectors.toSet());
        var latestCooldown = statuses.stream()
                .filter(status -> problemSystemIds.contains(status.systemId()))
                .map(FunctionalTestStatus::startedAt)
                .filter(value -> nonNull(value))
                .map(startedAt -> startedAt.plus(FunctionalTestCache.MANUAL_COOLDOWN))
                .max(Comparator.naturalOrder())
                .orElse(now);
        return latestCooldown.isAfter(now) ? latestCooldown : now;
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
