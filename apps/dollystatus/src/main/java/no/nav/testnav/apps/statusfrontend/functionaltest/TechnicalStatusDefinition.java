package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusDescriptor;
import reactor.core.publisher.Mono;

public interface TechnicalStatusDefinition {

    TechnicalStatusDescriptor descriptor();

    default boolean requiresPdl() {
        return false;
    }

    Mono<Void> check(FunctionalTestContext context);
}
