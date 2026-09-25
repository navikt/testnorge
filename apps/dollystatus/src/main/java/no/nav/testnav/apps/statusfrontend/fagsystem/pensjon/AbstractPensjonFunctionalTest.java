package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import java.util.Optional;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PensjonFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestExistingDataException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

abstract class AbstractPensjonFunctionalTest
        implements FunctionalTestDefinition<Preflight, Creation, Verification> {

    private final PensjonFunctionalTestProperties properties;
    private final Scheduler scheduler;

    protected AbstractPensjonFunctionalTest(
            PensjonFunctionalTestProperties properties,
            Scheduler scheduler
    ) {
        this.properties = properties;
        this.scheduler = scheduler;
    }

    @Override
    public final boolean requiresPdl() {
        return true;
    }

    @Override
    public final Mono<Preflight> preflight(FunctionalTestContext context) {
        return Mono.defer(() -> getResourceStatus(context))
                .flatMap(status -> status.empty()
                        ? Mono.just(Preflight.COMPLETED)
                        : Mono.error(new FunctionalTestExistingDataException()));
    }

    @Override
    public final Mono<Void> cleanupExistingData(FunctionalTestContext context) {
        return cleanup(context, Preflight.COMPLETED, Optional.empty(), Optional.empty(),
                descriptor().expectedCleanupState());
    }

    @Override
    public final Mono<Creation> create(
            FunctionalTestContext context,
            Preflight preflightResult
    ) {
        return Mono.defer(() -> createResource(context))
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public final Mono<Verification> verify(
            FunctionalTestContext context,
            Preflight preflightResult,
            Creation createResult
    ) {
        return awaitResourceState(context, true)
                .thenReturn(Verification.COMPLETED);
    }

    @Override
    public final Mono<Void> cleanup(
            FunctionalTestContext context,
            Preflight preflightResult,
            Optional<Creation> createResult,
            Optional<Verification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        return Mono.defer(() -> deleteResource(context))
                .then(awaitResourceState(context, false));
    }

    protected abstract Mono<Void> createResource(FunctionalTestContext context);

    protected abstract Mono<PensjonResourceStatus> getResourceStatus(FunctionalTestContext context);

    protected abstract Mono<Void> deleteResource(FunctionalTestContext context);

    private Mono<Void> awaitResourceState(FunctionalTestContext context, boolean expectedPresent) {
        return pollUntil(
                () -> getResourceStatus(context),
                status -> expectedPresent ? status.expectedDataPresent() : status.empty(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
