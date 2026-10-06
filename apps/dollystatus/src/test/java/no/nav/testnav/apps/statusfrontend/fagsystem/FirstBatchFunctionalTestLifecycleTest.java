package no.nav.testnav.apps.statusfrontend.fagsystem;

import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.ArbeidssoekerregisteretFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.InstdataFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataEnvironments;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestExistingDataException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FirstBatchFunctionalTestLifecycleTest {

    private static final String IDENT = "03458537037";
    private static final Instant STARTED_AT = Instant.parse("2026-09-21T10:00:00Z");
    private static final RunId RUN_ID = RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");

    @Mock
    private ArbeidssoekerregisteretClient arbeidssoekerregisteretClient;

    @Mock
    private InstdataClient instdataClient;

    private PdlFunctionalTestProperties pdlProperties;
    private VirtualTimeScheduler scheduler;

    @BeforeEach
    void setUp() {
        pdlProperties = new PdlFunctionalTestProperties();
        pdlProperties.setIdent(IDENT);
        scheduler = VirtualTimeScheduler.create();
    }

    @Test
    void shouldCleanupExistingArbeidssoekerRegistrationBeforeNewPreflight() {
        when(arbeidssoekerregisteretClient.getRegistration(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new ArbeidssoekerregisteretResourceStatus(false, false)),
                        Mono.just(ArbeidssoekerregisteretResourceStatus.emptyStatus()));
        when(arbeidssoekerregisteretClient.deleteRegistration(RUN_ID)).thenReturn(Mono.empty());
        var lifecycle = new ArbeidssoekerregisteretFunctionalTest(
                arbeidssoekerregisteretClient,
                pdlProperties,
                new ArbeidssoekerregisteretFunctionalTestProperties(),
                scheduler);

        var context = context("arbeidssoekerregisteret", FunctionalTestEnvironment.GLOBAL);
        StepVerifier.create(lifecycle.preflight(context))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();
        var calls = inOrder(arbeidssoekerregisteretClient);
        calls.verify(arbeidssoekerregisteretClient).getRegistration(eq(RUN_ID), any());
        calls.verify(arbeidssoekerregisteretClient).deleteRegistration(RUN_ID);
        calls.verify(arbeidssoekerregisteretClient, org.mockito.Mockito.times(2))
                .getRegistration(eq(RUN_ID), any());
    }

    @ParameterizedTest
    @EnumSource(value = FunctionalTestEnvironment.class, names = {"Q1", "Q2"})
    void shouldCleanupExistingInstdataOnlyInSelectedEnvironment(FunctionalTestEnvironment environment) {
        var environmentName = environment.name().toLowerCase(java.util.Locale.ROOT);
        when(instdataClient.getEnvironments(RUN_ID))
                .thenReturn(Mono.just(new InstdataEnvironments(List.of("q1", "q2"), List.of("q2"))));
        when(instdataClient.getInstdata(eq(RUN_ID), eq(IDENT), eq(environmentName), any()))
                .thenReturn(Mono.just(new InstdataResourceStatus(false, false)),
                        Mono.just(new InstdataResourceStatus(true, false)));
        when(instdataClient.deleteInstdata(RUN_ID, IDENT, List.of(environmentName))).thenReturn(Mono.empty());
        var lifecycle = new InstdataFunctionalTest(instdataClient, pdlProperties,
                new InstdataFunctionalTestProperties(), scheduler);
        var context = context("instdata", environment);

        StepVerifier.create(lifecycle.preflight(context))
                .expectError(FunctionalTestExistingDataException.class).verify();
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();
        verify(instdataClient).deleteInstdata(RUN_ID, IDENT, List.of(environmentName));
    }

    @Test
    void shouldNotRequestInstdataCleanupForUnsupportedEnvironment() {
        when(instdataClient.getEnvironments(RUN_ID))
                .thenReturn(Mono.just(new InstdataEnvironments(List.of("q1"), List.of("q2"))));
        when(instdataClient.getInstdata(eq(RUN_ID), eq(IDENT), eq("q2"), any()))
                .thenReturn(Mono.just(new InstdataResourceStatus(false, false)));
        var lifecycle = new InstdataFunctionalTest(instdataClient, pdlProperties,
                new InstdataFunctionalTestProperties(), scheduler);

        StepVerifier.create(lifecycle.preflight(context("instdata", FunctionalTestEnvironment.Q2)))
                .expectError(FunctionalTestBlockedException.class).verify();
        verify(instdataClient, never()).deleteInstdata(any(), any(), any());
    }

    @Test
    void shouldTimeoutInstdataVerification() {
        var properties = new InstdataFunctionalTestProperties();
        properties.setPollInterval(Duration.ofSeconds(1));
        properties.setPollTimeout(Duration.ofSeconds(5));
        when(instdataClient.getEnvironments(RUN_ID))
                .thenReturn(Mono.just(new InstdataEnvironments(List.of("q1", "q2"), List.of("q2"))));
        when(instdataClient.getInstdata(eq(RUN_ID), eq(IDENT), eq("q1"), any()))
                .thenReturn(Mono.just(new InstdataResourceStatus(true, false)));
        when(instdataClient.createInstdata(eq(RUN_ID), eq("q1"), any()))
                .thenReturn(Mono.empty());
        var lifecycle = new InstdataFunctionalTest(
                instdataClient,
                pdlProperties,
                properties,
                scheduler);
        var context = context("instdata", FunctionalTestEnvironment.Q1);

        StepVerifier.withVirtualTime(() -> lifecycle.preflight(context)
                        .flatMap(preflight -> lifecycle.create(context, preflight)
                                .flatMap(created -> lifecycle.verify(context, preflight, created))),
                        () -> scheduler,
                        Long.MAX_VALUE)
                .thenAwait(Duration.ofSeconds(5))
                .expectError(FunctionalTestVerificationTimeoutException.class)
                .verify();
    }

    private static FunctionalTestContext context(
            String systemId,
            FunctionalTestEnvironment environment
    ) {
        return new FunctionalTestContext(
                RUN_ID,
                new SystemId(systemId),
                environment,
                STARTED_AT);
    }
}
