package no.nav.testnav.apps.statusfrontend.fagsystem.instdata;

import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestPoller.pollUntil;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.InstdataFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestExistingDataException;
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
@ConditionalOnProperty(prefix = "functional-test.instdata", name = "enabled", havingValue = "true")
public class InstdataFunctionalTest
        implements FunctionalTestDefinition<InstdataPreflight, Creation, Verification> {

    private static final FunctionalTestDescriptor DESCRIPTOR = new FunctionalTestDescriptor(
            new SystemId("instdata"),
            new DisplayName("Instdata"),
            Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2),
            CleanupExpectation.DELETED);

    private final InstdataClient client;
    private final PdlFunctionalTestProperties pdlProperties;
    private final InstdataFunctionalTestProperties properties;
    private final Scheduler scheduler;

    public InstdataFunctionalTest(
            InstdataClient client,
            PdlFunctionalTestProperties pdlProperties,
            InstdataFunctionalTestProperties properties,
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
    public Mono<InstdataPreflight> preflight(FunctionalTestContext context) {
        var environment = environmentName(context.environment());
        var record = testRecord(context);
        return client.getEnvironments(context.runId())
                .filter(environments ->
                        environments.institusjonsoppholdEnvironments().contains(environment))
                .switchIfEmpty(Mono.error(new FunctionalTestBlockedException()))
                .then(client.getInstdata(
                        context.runId(),
                        pdlProperties.getIdent(),
                        environment,
                        record))
                .flatMap(status -> status.empty()
                        ? Mono.just(new InstdataPreflight(record))
                        : Mono.error(new FunctionalTestExistingDataException()));
    }

    @Override
    public Mono<Void> cleanupExistingData(FunctionalTestContext context) {
        return cleanup(context, new InstdataPreflight(testRecord(context)),
                Optional.empty(), Optional.empty(), DESCRIPTOR.expectedCleanupState());
    }

    @Override
    public Mono<Creation> create(
            FunctionalTestContext context,
            InstdataPreflight preflightResult
    ) {
        return client.createInstdata(
                        context.runId(),
                        environmentName(context.environment()),
                        preflightResult.record())
                .thenReturn(Creation.COMPLETED);
    }

    @Override
    public Mono<Verification> verify(
            FunctionalTestContext context,
            InstdataPreflight preflightResult,
            Creation createResult
    ) {
        return awaitStatus(context, preflightResult.record(), true)
                .thenReturn(Verification.COMPLETED);
    }

    @Override
    public Mono<Void> cleanup(
            FunctionalTestContext context,
            InstdataPreflight preflightResult,
            Optional<Creation> createResult,
            Optional<Verification> verificationResult,
            CleanupExpectation expectedEndState
    ) {
        var environment = environmentName(context.environment());
        return client.deleteInstdata(
                        context.runId(),
                        pdlProperties.getIdent(),
                        List.of(environment))
                .then(awaitStatus(context, preflightResult.record(), false));
    }

    private Mono<Void> awaitStatus(
            FunctionalTestContext context,
            InstdataRecord record,
            boolean expectedPresent
    ) {
        var environment = environmentName(context.environment());
        return pollUntil(
                () -> client.getInstdata(
                        context.runId(),
                        pdlProperties.getIdent(),
                        environment,
                        record),
                status -> expectedPresent ? status.expectedDataPresent() : status.empty(),
                properties.getPollInterval(),
                        properties.getPollTimeout(),
                        scheduler).then();
    }

    private InstdataRecord testRecord(FunctionalTestContext context) {
        var startDate = context.startedAt().atZone(ZoneOffset.UTC).toLocalDate();
        return new InstdataRecord(
                pdlProperties.getIdent(),
                "80000464106",
                "AS",
                "A",
                startDate,
                startDate.plusDays(2),
                startDate.plusDays(2),
                "Dolly");
    }

    private String environmentName(FunctionalTestEnvironment environment) {
        return environment.name().toLowerCase(java.util.Locale.ROOT);
    }
}
