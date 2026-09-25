package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub;

import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.BrregstubFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
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
@ConditionalOnProperty(prefix = "functional-test.brregstub", name = "enabled", havingValue = "true")
public class BrregstubFunctionalTest implements FunctionalTestDefinition<
        Preflight,
        Creation,
        Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("brregstub"),
            new DisplayName("Brregstub"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.DELETED);

    private final BrregstubClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final BrregstubFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public BrregstubFunctionalTest(
            BrregstubClient client,
            PdlFunctionalTestProperties pdlProperties,
            BrregstubFunctionalTestProperties properties,
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
        var request = BrregstubTestData.request(pdlProperties.getIdent(), context);
        return Mono.zip(client.getRoleOverview(request), client.getOrganization(request))
                .flatMap(statuses -> {
                    if (!statuses.getT2().empty() && !statuses.getT2().expectedDataPresent()) {
                        return Mono.error(new FunctionalTestBlockedException());
                    }
                    return statuses.getT1().empty() && statuses.getT2().empty()
                            ? Mono.just(Preflight.COMPLETED)
                            : Mono.error(new FunctionalTestExistingDataException());
                });
    }

    @Override
    public Mono<Void> cleanupExistingData(FunctionalTestContext context) {
        return cleanup(context, Preflight.COMPLETED, Optional.empty(), Optional.empty(),
                DESCRIPTOR.expectedCleanupState());
    }

    @Override
    public Mono<Creation> create(
            FunctionalTestContext context,
            Preflight preflightResult
    ) {
        var request = BrregstubTestData.request(pdlProperties.getIdent(), context);
        return client.createRoleOverview(request)
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<Verification> verify(
            FunctionalTestContext context,
            Preflight preflightResult,
            Creation createResult
    ) {
        return awaitCreated(context)
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
        var request = BrregstubTestData.request(pdlProperties.getIdent(), context);
        return client.deleteRoleOverview(pdlProperties.getIdent())
                .then(client.getOrganization(request))
                .flatMap(status -> {
                    if (status.empty()) {
                        return Mono.empty();
                    }
                    return status.expectedDataPresent()
                            ? client.deleteOrganization()
                            : Mono.error(new FunctionalTestBlockedException());
                })
                .then(awaitDeleted(context));
    }

    private Mono<Void> awaitCreated(FunctionalTestContext context) {
        var request = BrregstubTestData.request(pdlProperties.getIdent(), context);
        return awaitStatus(request, true);
    }

    private Mono<Void> awaitDeleted(FunctionalTestContext context) {
        var request = BrregstubTestData.request(pdlProperties.getIdent(), context);
        return awaitStatus(request, false);
    }

    private Mono<Void> awaitStatus(BrregstubRequest request, boolean expectedPresent) {
        return pollUntil(
                () -> Mono.zip(
                        client.getRoleOverview(request),
                        client.getOrganization(request)),
                statuses -> expectedPresent
                        ? statuses.getT1().expectedDataPresent() && !statuses.getT2().empty()
                        : statuses.getT1().empty() && statuses.getT2().empty(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
