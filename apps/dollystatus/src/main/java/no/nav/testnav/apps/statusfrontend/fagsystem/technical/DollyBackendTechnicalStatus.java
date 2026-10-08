package no.nav.testnav.apps.statusfrontend.fagsystem.technical;

import no.nav.testnav.apps.statusfrontend.functionaltest.TechnicalStatusDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusDescriptor;
import reactor.core.publisher.Mono;

import java.util.Set;

public abstract class DollyBackendTechnicalStatus implements TechnicalStatusDefinition {

    private final DollyBackendStatusClient client;
    private final String consumerName;
    private final TechnicalStatusDescriptor descriptor;

    protected DollyBackendTechnicalStatus(
            DollyBackendStatusClient client,
            String systemId,
            String displayName,
            String consumerName
    ) {
        this.client = client;
        this.consumerName = consumerName;
        descriptor = new TechnicalStatusDescriptor(
                new SystemId(systemId),
                new DisplayName(displayName),
                Set.of(FunctionalTestEnvironment.GLOBAL));
    }

    @Override
    public TechnicalStatusDescriptor descriptor() {
        return descriptor;
    }

    @Override
    public Mono<Void> check(FunctionalTestContext context) {
        return client.check(consumerName);
    }
}
