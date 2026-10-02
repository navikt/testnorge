package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PensjonFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.Set;

@Service
@ConditionalOnProperty(prefix = "functional-test.pensjon.tp", name = "enabled", havingValue = "true")
public class TpForholdFunctionalTest extends AbstractPensjonFunctionalTest {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("pensjon-tp"),
            new DisplayName("Pensjon TP-forhold"),
            Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2),
            CleanupExpectation.DELETED);

    private final PensjonClient client;

    public TpForholdFunctionalTest(
            PensjonClient client,
            PensjonFunctionalTestProperties properties,
            Scheduler scheduler
    ) {
        super(properties, scheduler);
        this.client = client;
    }

    @Override
    public FunctionalTestDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    protected Mono<Void> createResource(FunctionalTestContext context) {
        return client.createTpForhold(context.environment(), context.runId());
    }

    @Override
    protected Mono<PensjonResourceStatus> getResourceStatus(FunctionalTestContext context) {
        return client.getTpForhold(context.environment(), context.runId());
    }

    @Override
    protected Mono<Void> deleteResource(FunctionalTestContext context) {
        return client.deleteTpForhold(context.environment(), context.runId());
    }
}
