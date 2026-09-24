package no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister;

import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SkjermingsregisterFunctionalTestProperties;
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
        prefix = "functional-test.skjermingsregister",
        name = "enabled",
        havingValue = "true")
public class SkjermingsregisterFunctionalTest implements FunctionalTestDefinition<
        SkjermingsregisterPreflight,
        Creation,
        Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("skjermingsregister"),
            new DisplayName("Skjermingsregister"),
            Set.of(FunctionalTestEnvironment.GLOBAL),
            CleanupExpectation.TERMINATED);

    private final SkjermingsregisterClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final SkjermingsregisterFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public SkjermingsregisterFunctionalTest(
            SkjermingsregisterClient client,
            PdlFunctionalTestProperties pdlProperties,
            SkjermingsregisterFunctionalTestProperties properties,
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
    public Mono<SkjermingsregisterPreflight> preflight(FunctionalTestContext context) {
        var request = SkjermingsregisterTestData.activeRequest(
                pdlProperties.getIdent(),
                context);
        return client.getScreening(request, referenceTime(context))
                .flatMap(status -> {
                    if (status.empty()) {
                        return Mono.just(new SkjermingsregisterPreflight(false));
                    }
                    if (status.owned() && status.terminated()) {
                        return Mono.just(new SkjermingsregisterPreflight(true));
                    }
                    return Mono.error(new FunctionalTestBlockedException());
                });
    }

    @Override
    public Mono<Creation> create(
            FunctionalTestContext context,
            SkjermingsregisterPreflight preflightResult
    ) {
        var request = SkjermingsregisterTestData.activeRequest(
                pdlProperties.getIdent(),
                context);
        var create = preflightResult.existingOwnedRecord()
                ? client.updateScreening(request)
                : client.createScreening(request);
        return create.thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<Verification> verify(
            FunctionalTestContext context,
            SkjermingsregisterPreflight preflightResult,
            Creation createResult
    ) {
        return awaitActive(context)
                .thenReturn(Verification.COMPLETED);
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            SkjermingsregisterPreflight preflightResult,
            Optional<Creation> createResult,
            Optional<Verification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        var request = SkjermingsregisterTestData.terminatedRequest(
                pdlProperties.getIdent(),
                context);
        return client.updateScreening(request)
                .then(awaitTerminated(context, createResult.isEmpty()));
    }

    private Mono<Void> awaitActive(FunctionalTestContext context) {
        var request = SkjermingsregisterTestData.activeRequest(
                pdlProperties.getIdent(),
                context);
        return awaitStatus(context, request, false);
    }

    private Mono<Void> awaitTerminated(
            FunctionalTestContext context,
            boolean allowEmpty
    ) {
        var request = SkjermingsregisterTestData.terminatedRequest(
                pdlProperties.getIdent(),
                context);
        return awaitStatus(context, request, allowEmpty);
    }

    private Mono<Void> awaitStatus(
            FunctionalTestContext context,
            SkjermingsregisterRequest expectedRequest,
            boolean allowEmpty
    ) {
        var expectedActive = expectedRequest.skjermetTil()
                .isAfter(expectedRequest.skjermetFra());
        return pollUntil(
                () -> client.getScreening(expectedRequest, referenceTime(context)),
                status -> {
                    if (allowEmpty && status.empty()) {
                        return true;
                    }
                    return expectedActive
                            ? status.active() && status.expectedDataPresent()
                            : status.terminated() && status.expectedDataPresent();
                },
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }

    private static java.time.LocalDateTime referenceTime(FunctionalTestContext context) {
        return context.startedAt().atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}
