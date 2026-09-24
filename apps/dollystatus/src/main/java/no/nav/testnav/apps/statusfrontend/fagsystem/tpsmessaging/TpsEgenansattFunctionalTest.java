package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.TpsMessagingFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.CleanupExpectation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
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
@ConditionalOnProperty(
        prefix = "functional-test.tps-messaging-egenansatt",
        name = "enabled",
        havingValue = "true")
public class TpsEgenansattFunctionalTest implements FunctionalTestDefinition<
        TpsEgenansattPreflight,
        Creation,
        Verification> {

    private static final List<String> ENVIRONMENTS = List.of("q1", "q2");
    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("tps-messaging-egenansatt"),
            new DisplayName("TPS Messaging egenansatt"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.INACTIVE);

    private final TpsMessagingClient client;
    private final TpsMessagingFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public TpsEgenansattFunctionalTest(
            TpsMessagingClient client,
            TpsMessagingFunctionalTestProperties properties,
            Scheduler scheduler
    ) {
        this.client = client;
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
    public Mono<TpsEgenansattPreflight> preflight(FunctionalTestContext context) {
        var fromDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        return client.getEgenansatt(context.runId(), ENVIRONMENTS, fromDate)
                .flatMap(status -> status.allEnvironmentsPresent() && status.inactive()
                        ? Mono.just(new TpsEgenansattPreflight(fromDate))
                        : Mono.error(new FunctionalTestBlockedException()));
    }

    @Override
    public Mono<Creation> create(
            FunctionalTestContext context,
            TpsEgenansattPreflight preflightResult
    ) {
        return client.createEgenansatt(
                        context.runId(),
                        ENVIRONMENTS,
                        preflightResult.fromDate())
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<Verification> verify(
            FunctionalTestContext context,
            TpsEgenansattPreflight preflightResult,
            Creation createResult
    ) {
        return awaitStatus(context, preflightResult.fromDate(), true)
                .thenReturn(Verification.COMPLETED);
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            TpsEgenansattPreflight preflightResult,
            Optional<Creation> createResult,
            Optional<Verification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        return client.deleteEgenansatt(context.runId(), ENVIRONMENTS)
                .then(awaitStatus(context, preflightResult.fromDate(), false));
    }

    private Mono<Void> awaitStatus(
            FunctionalTestContext context,
            java.time.LocalDate fromDate,
            boolean expectedPresent
    ) {
        return pollUntil(
                () -> client.getEgenansatt(context.runId(), ENVIRONMENTS, fromDate),
                status -> expectedPresent
                        ? status.expectedDataPresent()
                        : status.allEnvironmentsPresent() && status.inactive(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }
}
