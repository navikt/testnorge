package no.nav.testnav.apps.statusfrontend.fagsystem.nom;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.NomFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import static java.util.Objects.nonNull;

@Service
@ConditionalOnProperty(prefix = "functional-test.nom", name = "enabled", havingValue = "true")
public class NomFunctionalTest
        implements FunctionalTestDefinition<NomFunctionalTest.Preflight, Creation, NomVerification> {

    public record Preflight(NomResourceStatus previousStatus) {
    }

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("nom"),
            new DisplayName("NOM"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.TERMINATED);

    private final NomClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final NomFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public NomFunctionalTest(
            NomClient client,
            PdlFunctionalTestProperties pdlProperties,
            NomFunctionalTestProperties properties,
            Scheduler scheduler
    ) {
        this.client = client;
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.scheduler = scheduler;
    }

    @Override
    public FunctionalTestDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public boolean requiresPdl() {
        return true;
    }

    @Override
    public Mono<Preflight> preflight(FunctionalTestContext context) {
        var request = NomTestData.request(pdlProperties.getIdent(), context);
        return client.getResource(context.runId(), request)
                .flatMap(status -> status.empty() || isInactive(status, context)
                        ? Mono.just(new Preflight(status))
                        : Mono.error(new FunctionalTestBlockedException()));
    }

    @Override
    public Mono<Creation> create(FunctionalTestContext context, Preflight preflightResult) {
        var request = NomTestData.request(pdlProperties.getIdent(), context);
        return client.createResource(context.runId(), request)
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<NomVerification> verify(
            FunctionalTestContext context,
            Preflight preflightResult,
            Creation createResult
    ) {
        return awaitStatus(context, preflightResult, true, null)
                .map(status -> new NomVerification(status.resourceId()));
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            Preflight preflightResult,
            Optional<Creation> createResult,
            Optional<NomVerification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        if (verificationResult.isPresent()) {
            return closeAndVerify(context, preflightResult, verificationResult.get().resourceId());
        }
        var request = NomTestData.request(pdlProperties.getIdent(), context);
        return client.getResource(context.runId(), request)
                .flatMap(status -> {
                    if (status.empty() || isInactive(status, context)) {
                        return Mono.empty();
                    }
                    if (!matchesCreatedResource(status, request, preflightResult)) {
                        return Mono.error(new FunctionalTestBlockedException());
                    }
                    return closeAndVerify(context, preflightResult, status.resourceId());
                });
    }

    private Mono<Void> closeAndVerify(
            FunctionalTestContext context,
            Preflight preflight,
            String resourceId
    ) {
        var endDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate().minusDays(1);
        return client.closeResource(context.runId(), endDate)
                .then(awaitStatus(context, preflight, false, resourceId))
                .then();
    }

    private static boolean isInactive(NomResourceStatus status, FunctionalTestContext context) {
        return status.closed()
                && nonNull(status.endDate())
                && status.endDate().isBefore(context.startedAt().atZone(ZoneOffset.UTC).toLocalDate());
    }

    private Mono<NomResourceStatus> awaitStatus(
            FunctionalTestContext context,
            Preflight preflight,
            boolean expectedActive,
            String expectedResourceId
    ) {
        var request = NomTestData.request(pdlProperties.getIdent(), context);
        var expectedEndDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate().minusDays(1);
        return pollUntil(
                () -> client.getResource(context.runId(), request),
                status -> expectedActive
                        ? matchesCreatedResource(status, request, preflight)
                        : status.closed()
                        && expectedEndDate.equals(status.endDate())
                        && nonNull(expectedResourceId)
                        && expectedResourceId.equals(status.resourceId()),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler);
    }

    private static boolean matchesCreatedResource(
            NomResourceStatus status,
            NomRequest request,
            Preflight preflight
    ) {
        var previous = preflight.previousStatus();
        var retainedStartDate = !previous.empty()
                && nonNull(previous.resourceId())
                && previous.resourceId().equals(status.resourceId())
                && nonNull(previous.startDate())
                && previous.startDate().equals(status.startDate());
        return status.expectedPersonPresent()
                && !status.closed()
                && (request.startDato().equals(status.startDate()) || retainedStartDate);
    }
}
