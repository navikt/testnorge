package no.nav.testnav.apps.statusfrontend.fagsystem.oppfoelgingsvedtak14a;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.SecondBatchTechnicalStatusClient;
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
@ConditionalOnProperty(
        prefix = "functional-test.oppfoelgingsvedtak-14a",
        name = "technical-only",
        havingValue = "true")
public class Oppfoelgingsvedtak14aTechnicalStatus implements TechnicalStatusDefinition {

    private static final TechnicalStatusDescriptor DESCRIPTOR = new TechnicalStatusDescriptor(
            new SystemId("oppfoelgingsvedtak-14a"),
            new DisplayName("Oppfølgingsvedtak 14a"),
            Set.of(FunctionalTestEnvironment.GLOBAL));

    private final SecondBatchTechnicalStatusClient client;

    public Oppfoelgingsvedtak14aTechnicalStatus(SecondBatchTechnicalStatusClient client) {
        this.client = client;
    }

    @Override
    public TechnicalStatusDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Mono<Void> check(FunctionalTestContext context) {
        return client.checkDollyProxy();
    }
}
