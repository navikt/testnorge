package no.nav.testnav.apps.statusfrontend.fagsystem.skattekort;

import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SkattekortFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

@Service
@ConditionalOnProperty(prefix = "functional-test.skattekort", name = "enabled", havingValue = "true")
public class SkattekortFunctionalTest
        implements FunctionalTestDefinition<Preflight, Creation, Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("skattekort"),
            new DisplayName("Skattekort"),
            Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2),
            CleanupExpectation.NOT_TAX_CARD);

    private final SkattekortClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final SkattekortFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public SkattekortFunctionalTest(
            SkattekortClient client,
            PdlFunctionalTestProperties pdlProperties,
            SkattekortFunctionalTestProperties properties,
            Scheduler scheduler
    ) {
        this.client = client;
        this.pdlProperties = pdlProperties;
        this.properties = properties;
        this.scheduler = scheduler;
    }

    @Override
    public FunctionalTestDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public boolean requiresPdl() {
        return true;
    }

    @Override
    public Mono<Preflight> preflight(FunctionalTestContext context) {
        return client.getTaxCard(
                        context.environment(),
                        context.runId(),
                        SkattekortTestData.incomeYear(context))
                .flatMap(status -> status.empty() || status.notTaxCard()
                        ? Mono.just(Preflight.COMPLETED)
                        : Mono.error(new FunctionalTestBlockedException()));
    }

    @Override
    public Mono<Creation> create(
            FunctionalTestContext context,
            Preflight preflightResult
    ) {
        var request = SkattekortTestData.taxCard(pdlProperties.getIdent(), context);
        return client.createTaxCard(context.environment(), context.runId(), request)
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<Verification> verify(
            FunctionalTestContext context,
            Preflight preflightResult,
            Creation createResult
    ) {
        return awaitStatus(context, true)
                .thenReturn(Verification.COMPLETED);
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            Preflight preflightResult,
            Optional<Creation> createResult,
            Optional<Verification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        var request = SkattekortTestData.notTaxCard(pdlProperties.getIdent(), context);
        return client.createTaxCard(context.environment(), context.runId(), request)
                .then(awaitStatus(context, false));
    }

    private Mono<Void> awaitStatus(FunctionalTestContext context, boolean expectedTaxCard) {
        return pollUntil(
                () -> client.getTaxCard(
                        context.environment(),
                        context.runId(),
                        SkattekortTestData.incomeYear(context)),
                status -> expectedTaxCard
                        ? status.expectedTaxCardPresent()
                        : status.notTaxCard(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
