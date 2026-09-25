package no.nav.testnav.apps.statusfrontend.fagsystem.sigrun;

import no.nav.testnav.apps.statusfrontend.functionaltest.TechnicalStatusDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusDescriptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@ConditionalOnProperty(prefix = "functional-test.sigrun", name = "technical-only", havingValue = "true")
public class SigrunTechnicalStatus implements TechnicalStatusDefinition {

    private static final TechnicalStatusDescriptor DESCRIPTOR = new TechnicalStatusDescriptor(
            new SystemId("sigrun"),
            new DisplayName("Sigrun"),
            Set.of(FunctionalTestEnvironment.GLOBAL));

    private final SigrunTechnicalStatusClient client;

    public SigrunTechnicalStatus(SigrunTechnicalStatusClient client) {
        this.client = client;
    }

    @Override
    public TechnicalStatusDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Mono<Void> check(FunctionalTestContext context) {
        return client.checkReadiness();
    }
}
