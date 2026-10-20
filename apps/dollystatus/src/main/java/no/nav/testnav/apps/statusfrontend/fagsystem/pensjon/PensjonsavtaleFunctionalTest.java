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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.Set;

@Service
@ConditionalOnProperty(prefix = "functional-test.pensjon.pensjonsavtale", name = "enabled", havingValue = "true")
public class PensjonsavtaleFunctionalTest extends AbstractPensjonFunctionalTest {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("pensjon-pensjonsavtale"),
            new DisplayName("Pensjon pensjonsavtale"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.DELETED);
    private static final List<FunctionalTestEnvironment> PEN_ENVIRONMENTS = List.of(
            FunctionalTestEnvironment.Q1,
            FunctionalTestEnvironment.Q2);

    private final PensjonClient client;

    public PensjonsavtaleFunctionalTest(
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
        return client.createPensjonsavtale(context.runId());
    }

    @Override
    protected Mono<PensjonResourceStatus> getResourceStatus(FunctionalTestContext context) {
        return Flux.fromIterable(PEN_ENVIRONMENTS)
                .concatMap(environment -> client.getPensjonsavtale(environment, context.runId()))
                .collectList()
                .map(statuses -> new PensjonResourceStatus(
                        statuses.stream().allMatch(PensjonResourceStatus::empty),
                        statuses.stream().allMatch(PensjonResourceStatus::expectedDataPresent)));
    }

    @Override
    protected Mono<Void> deleteResource(FunctionalTestContext context) {
        return client.deletePensjonsavtale(context.runId());
    }
}
