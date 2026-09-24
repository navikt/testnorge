package no.nav.testnav.apps.statusfrontend.fagsystem;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.ArbeidssoekerregisteretFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.InstdataFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.TpsMessagingFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arbeidssoekerregisteret.ArbeidssoekerregisteretResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataEnvironments;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.TpsEgenansattFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.TpsEgenansattResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging.TpsMessagingClient;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
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

    @Mock
    private TpsMessagingClient tpsMessagingClient;

    private PdlFunctionalTestProperties pdlProperties;
    private VirtualTimeScheduler scheduler;

    @BeforeEach
    void setUp() {
        pdlProperties = new PdlFunctionalTestProperties();
        pdlProperties.setIdent(IDENT);
        scheduler = VirtualTimeScheduler.create();
    }

    @Test
    void shouldBlockArbeidssoekerregisteretWhenRegistrationAlreadyExists() {
        when(arbeidssoekerregisteretClient.getRegistration(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new ArbeidssoekerregisteretResourceStatus(false, false)));
        var lifecycle = new ArbeidssoekerregisteretFunctionalTest(
                arbeidssoekerregisteretClient,
                pdlProperties,
                new ArbeidssoekerregisteretFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(context(
                        "arbeidssoekerregisteret",
                        FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
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

    @Test
    void shouldCleanupTpsEgenansattAfterVerifyFailure() {
        var properties = new TpsMessagingFunctionalTestProperties();
        properties.setPollInterval(Duration.ofSeconds(1));
        properties.setPollTimeout(Duration.ofSeconds(5));
        var fromDate = LocalDate.of(2026, 9, 21);
        var environments = List.of("q1", "q2");
        when(tpsMessagingClient.getEgenansatt(RUN_ID, environments, fromDate))
                .thenReturn(
                        Mono.just(new TpsEgenansattResourceStatus(true, false, true)),
                        Mono.error(new IllegalStateException("Verification failed.")),
                        Mono.just(new TpsEgenansattResourceStatus(true, false, true)));
        when(tpsMessagingClient.createEgenansatt(RUN_ID, environments, fromDate))
                .thenReturn(Mono.empty());
        when(tpsMessagingClient.deleteEgenansatt(RUN_ID, environments))
                .thenReturn(Mono.empty());
        var lifecycle = new TpsEgenansattFunctionalTest(
                tpsMessagingClient,
                properties,
                scheduler);
        var context = context("tps-messaging-egenansatt", FunctionalTestEnvironment.GLOBAL);

        StepVerifier.withVirtualTime(() -> lifecycle.preflight(context)
                        .flatMap(preflight -> lifecycle.create(context, preflight)
                                .flatMap(created -> lifecycle.verify(context, preflight, created)
                                        .flatMap(verified -> lifecycle.cleanup(
                                                context,
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                lifecycle.descriptor().expectedCleanupState()))
                                        .onErrorResume(throwable -> lifecycle.cleanup(
                                                        context,
                                                        preflight,
                                                        Optional.of(created),
                                                        Optional.empty(),
                                                        lifecycle.descriptor().expectedCleanupState())
                                                .then(Mono.error(throwable))))),
                        () -> scheduler,
                        Long.MAX_VALUE)
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && error.getMessage().equals("Verification failed."))
                .verify();

        var calls = inOrder(tpsMessagingClient);
        calls.verify(tpsMessagingClient).getEgenansatt(RUN_ID, environments, fromDate);
        calls.verify(tpsMessagingClient).createEgenansatt(RUN_ID, environments, fromDate);
        calls.verify(tpsMessagingClient).getEgenansatt(RUN_ID, environments, fromDate);
        calls.verify(tpsMessagingClient).deleteEgenansatt(RUN_ID, environments);
        calls.verify(tpsMessagingClient).getEgenansatt(RUN_ID, environments, fromDate);
        verify(tpsMessagingClient).deleteEgenansatt(RUN_ID, environments);
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
