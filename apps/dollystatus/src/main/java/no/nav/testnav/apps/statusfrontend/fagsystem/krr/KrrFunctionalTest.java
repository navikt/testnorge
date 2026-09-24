package no.nav.testnav.apps.statusfrontend.fagsystem.krr;

import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.KrrFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
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
@ConditionalOnProperty(prefix = "functional-test.krr", name = "enabled", havingValue = "true")
public class KrrFunctionalTest
        implements FunctionalTestDefinition<Preflight, Creation, Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("krr"),
            new DisplayName("KRR"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.DELETED);

    private final KrrClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final KrrFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public KrrFunctionalTest(
            KrrClient client,
            PdlFunctionalTestProperties pdlProperties,
            KrrFunctionalTestProperties properties,
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
        var request = KrrTestData.request(pdlProperties.getIdent(), context);
        return client.getContactInformation(context.runId(), request)
                .flatMap(status -> status.empty()
                        ? Mono.just(Preflight.COMPLETED)
                        : Mono.error(new FunctionalTestBlockedException()));
    }

    @Override
    public Mono<Creation> create(FunctionalTestContext context, Preflight preflightResult) {
        var request = KrrTestData.request(pdlProperties.getIdent(), context);
        return client.createContactInformation(context.runId(), request)
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
        return client.deleteContactInformation(context.runId())
                .then(awaitStatus(context, false));
    }

    private Mono<Void> awaitStatus(FunctionalTestContext context, boolean expectedPresent) {
        var request = KrrTestData.request(pdlProperties.getIdent(), context);
        return pollUntil(
                () -> client.getContactInformation(context.runId(), request),
                status -> expectedPresent ? status.expectedDataPresent() : status.empty(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
