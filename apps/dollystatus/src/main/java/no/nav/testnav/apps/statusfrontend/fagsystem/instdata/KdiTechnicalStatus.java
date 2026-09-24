package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

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
@ConditionalOnProperty(prefix = "functional-test.kdi", name = "enabled", havingValue = "true")
public class KdiTechnicalStatus implements TechnicalStatusDefinition {

    private static final TechnicalStatusDescriptor DESCRIPTOR = new TechnicalStatusDescriptor(
            new SystemId("kdi"),
            new DisplayName("KDI"),
            Set.of(FunctionalTestEnvironment.Q2));

    private final InstdataClient client;

    public KdiTechnicalStatus(InstdataClient client) {
        this.client = client;
    }

    @Override
    public TechnicalStatusDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Mono<Void> check(FunctionalTestContext context) {
        return client.getEnvironments(context.runId())
                .filter(environments -> environments.kdiEnvironments().contains("q2"))
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "KDI Q2 er ikke tilgjengelig i miljøkonfigurasjonen.")))
                .then();
    }
}
