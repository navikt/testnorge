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
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;

@Service
@ConditionalOnProperty(prefix = "functional-test.nom", name = "enabled", havingValue = "true")
public class NomFunctionalTest
        implements FunctionalTestDefinition<Preflight, Creation, NomVerification> {

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
                .flatMap(status -> status.empty() || status.closed()
                        ? Mono.just(Preflight.COMPLETED)
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
        return awaitStatus(context, true, null)
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
        var endDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        var resourceId = verificationResult.map(NomVerification::resourceId).orElse(null);
        return client.closeResource(context.runId(), endDate)
                .then(awaitStatus(context, false, resourceId))
                .then();
    }

    private Mono<NomResourceStatus> awaitStatus(
            FunctionalTestContext context,
            boolean expectedActive,
            String expectedResourceId
    ) {
        var request = NomTestData.request(pdlProperties.getIdent(), context);
        var expectedEndDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        return pollUntil(
                () -> client.getResource(context.runId(), request),
                status -> expectedActive
                        ? status.expectedDataPresent()
                        : status.closed()
                        && expectedEndDate.equals(status.endDate())
                        && expectedResourceId != null
                        && expectedResourceId.equals(status.resourceId()),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler);
    }
}
