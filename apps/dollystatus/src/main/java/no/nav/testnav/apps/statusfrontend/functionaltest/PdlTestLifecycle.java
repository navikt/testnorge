package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface PdlTestLifecycle<P, C> {

    FunctionalTestDescriptor descriptor();

    Mono<P> preflight(FunctionalTestContext context);

    Mono<C> create(FunctionalTestContext context, P preflightResult);

    Mono<Void> verify(
            FunctionalTestContext context,
            P preflightResult,
            C createResult,
            FunctionalTestEnvironment environment
    );

    Mono<Void> cleanup(
            FunctionalTestContext context,
            Optional<P> preflightResult,
            Optional<C> createResult
    );
}
