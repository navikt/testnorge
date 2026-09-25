package no.nav.testnav.apps.statusfrontend.fagsystem.arena;

import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.ArenaFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestExistingDataException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
@ConditionalOnProperty(prefix = "functional-test.arena", name = "enabled", havingValue = "true")
public class ArenaFunctionalTest
        implements FunctionalTestDefinition<Preflight, Creation, Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("arena"),
            new DisplayName("Arena"),
            Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2),
            CleanupExpectation.INACTIVE);

    private final ArenaClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final ArenaFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public ArenaFunctionalTest(
            ArenaClient client,
            PdlFunctionalTestProperties pdlProperties,
            ArenaFunctionalTestProperties properties,
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
        var request = ArenaTestData.request(pdlProperties.getIdent(), context);
        return client.getUser(context.environment(), context.runId(), request)
                .flatMap(status -> status.empty() || status.inactive()
                        ? Mono.just(Preflight.COMPLETED)
                        : Mono.error(new FunctionalTestExistingDataException()));
    }

    @Override
    public Mono<Void> cleanupExistingData(FunctionalTestContext context) {
        return cleanup(context, Preflight.COMPLETED, Optional.empty(), Optional.empty(),
                DESCRIPTOR.expectedCleanupState());
    }

    @Override
    public Mono<Creation> create(FunctionalTestContext context, Preflight preflightResult) {
        var request = ArenaTestData.request(pdlProperties.getIdent(), context);
        return client.createUser(context.environment(), context.runId(), request)
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<Verification> verify(
            FunctionalTestContext context,
            Preflight preflightResult,
            Creation createResult
    ) {
        return awaitStatus(context, true)
                .thenReturn(Verification.COMPLETED);
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            Preflight preflightResult,
            Optional<Creation> createResult,
            Optional<Verification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        return client.deactivateUser(context.environment(), context.runId())
                .then(awaitStatus(context, false));
    }

    private Mono<Void> awaitStatus(FunctionalTestContext context, boolean expectedActive) {
        var request = ArenaTestData.request(pdlProperties.getIdent(), context);
        return pollUntil(
                () -> client.getUser(context.environment(), context.runId(), request),
                status -> expectedActive ? status.expectedDataPresent() : status.inactive(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
