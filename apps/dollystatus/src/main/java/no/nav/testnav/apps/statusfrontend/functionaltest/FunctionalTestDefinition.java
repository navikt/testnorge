package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface FunctionalTestDefinition<P, C, V> {

    FunctionalTestDescriptor descriptor();

    default boolean requiresPdl() {
        return false;
    }

    Mono<P> preflight(FunctionalTestContext context);

    Mono<C> create(FunctionalTestContext context, P preflightResult);

    Mono<V> verify(FunctionalTestContext context, P preflightResult, C createResult);

    Mono<Void> cleanup(
            FunctionalTestContext context,
            P preflightResult,
            Optional<C> createResult,
            Optional<V> verificationResult,
            CleanupExpectation expectedEndState
    );
}
