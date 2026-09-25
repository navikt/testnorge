package no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister;

import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.KontoregisterFunctionalTestProperties;
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
@ConditionalOnProperty(
        prefix = "functional-test.kontoregister",
        name = "enabled",
        havingValue = "true")
public class KontoregisterFunctionalTest implements FunctionalTestDefinition<
        Preflight,
        Creation,
        Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("kontoregister"),
            new DisplayName("Kontoregister"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.DELETED);

    private final KontoregisterClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final KontoregisterFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public KontoregisterFunctionalTest(
            KontoregisterClient client,
            PdlFunctionalTestProperties pdlProperties,
            KontoregisterFunctionalTestProperties properties,
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
        var account = KontoregisterTestData.account(pdlProperties.getIdent());
        return client.getAccount(context.runId(), account)
                .flatMap(status -> status.empty()
                        ? Mono.just(Preflight.COMPLETED)
                        : Mono.error(new FunctionalTestExistingDataException()));
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
        var account = KontoregisterTestData.account(pdlProperties.getIdent());
        return client.createAccount(context.runId(), account)
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
        return client.deleteAccount(context.runId())
                .then(awaitStatus(context, false));
    }

    private Mono<Void> awaitStatus(FunctionalTestContext context, boolean expectedPresent) {
        var account = KontoregisterTestData.account(pdlProperties.getIdent());
        return pollUntil(
                () -> client.getAccount(context.runId(), account),
                status -> expectedPresent ? status.expectedDataPresent() : status.empty(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
