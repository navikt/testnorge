package no.nav.testnav.apps.statusfrontend.fagsystem;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.ArenaFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.KontoregisterFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.KrrFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.NomFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SkattekortFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.arena.ArenaResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.KontoregisterClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.KontoregisterFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.kontoregister.KontoregisterResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.krr.KrrResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecondBatchFunctionalTestLifecycleTest {

    private static final String IDENT = "03458537037";
    private static final Instant STARTED_AT = Instant.parse("2026-09-21T10:00:00Z");
    private static final RunId RUN_ID = RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");

    @Mock
    private ArenaClient arenaClient;

    @Mock
    private KontoregisterClient kontoregisterClient;

    @Mock
    private KrrClient krrClient;

    @Mock
    private NomClient nomClient;

    @Mock
    private SkattekortClient skattekortClient;

    private PdlFunctionalTestProperties pdlProperties;
    private VirtualTimeScheduler scheduler;

    @BeforeEach
    void setUp() {
        pdlProperties = new PdlFunctionalTestProperties();
        pdlProperties.setIdent(IDENT);
        scheduler = VirtualTimeScheduler.create();
    }

    @Test
    void shouldExposeSequentialEnvironmentModels() {
        var arena = new ArenaFunctionalTest(
                arenaClient,
                pdlProperties,
                new ArenaFunctionalTestProperties(),
                scheduler);
        var skattekort = new SkattekortFunctionalTest(
                skattekortClient,
                pdlProperties,
                new SkattekortFunctionalTestProperties(),
                scheduler);

        assertThat(arena.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2));
        assertThat(skattekort.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.Q1, FunctionalTestEnvironment.Q2));
    }

    @Test
    void shouldCreateKontoregisterAccountWithValidChecksum() {
        when(kontoregisterClient.createAccount(eq(RUN_ID), any())).thenReturn(Mono.empty());
        var lifecycle = new KontoregisterFunctionalTest(
                kontoregisterClient,
                pdlProperties,
                new KontoregisterFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.create(
                        context("kontoregister", FunctionalTestEnvironment.GLOBAL),
                        Preflight.COMPLETED))
                .expectNextCount(1)
                .verifyComplete();

        var captor = ArgumentCaptor.forClass(OppdaterKontoRequestDTO.class);
        verify(kontoregisterClient).createAccount(eq(RUN_ID), captor.capture());
        var accountNumber = captor.getValue().getKontonummer();
        assertThat(accountNumber).matches("[0-9]{11}").isNotEqualTo("00000000000");
        var weights = new int[]{5, 4, 3, 2, 7, 6, 5, 4, 3, 2, 1};
        var checksum = 0;
        for (var i = 0; i < weights.length; i++) {
            checksum += Character.digit(accountNumber.charAt(i), 10) * weights[i];
        }
        assertThat(checksum % 11).isZero();
    }

    @Test
    void shouldRequirePdlForKontoregister() {
        var lifecycle = new KontoregisterFunctionalTest(
                kontoregisterClient,
                pdlProperties,
                new KontoregisterFunctionalTestProperties(),
                scheduler);

        assertThat(lifecycle.requiresPdl()).isTrue();
    }

    @Test
    void shouldBlockArenaWhenActiveUserExists() {
        when(arenaClient.getUser(eq(FunctionalTestEnvironment.Q1), eq(RUN_ID), any()))
                .thenReturn(Mono.just(new ArenaResourceStatus(false, true, true, false)));
        var lifecycle = new ArenaFunctionalTest(
                arenaClient,
                pdlProperties,
                new ArenaFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(context("arena", FunctionalTestEnvironment.Q1)))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
    }

    @Test
    void shouldBlockKontoregisterWhenAnyActiveAccountExists() {
        when(kontoregisterClient.getAccount(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new KontoregisterResourceStatus(false, false)));
        var lifecycle = new KontoregisterFunctionalTest(
                kontoregisterClient,
                pdlProperties,
                new KontoregisterFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(
                        context("kontoregister", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
    }

    @Test
    void shouldBlockKrrWhenContactInformationExists() {
        when(krrClient.getContactInformation(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new KrrResourceStatus(false, false)));
        var lifecycle = krrLifecycle();

        StepVerifier.create(lifecycle.preflight(context("krr", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
    }

    @Test
    void shouldBlockNomWhenActiveResourceExists() {
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new NomResourceStatus(
                        false,
                        false,
                        false,
                        "12345",
                        null)));
        var lifecycle = new NomFunctionalTest(
                nomClient,
                pdlProperties,
                new NomFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(context("nom", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
    }

    @Test
    void shouldBlockSkattekortWhenTaxCardExists() {
        when(skattekortClient.getTaxCard(
                FunctionalTestEnvironment.Q2,
                RUN_ID,
                2026)).thenReturn(Mono.just(new SkattekortResourceStatus(
                false,
                true,
                false)));
        var lifecycle = new SkattekortFunctionalTest(
                skattekortClient,
                pdlProperties,
                new SkattekortFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(
                        context("skattekort", FunctionalTestEnvironment.Q2)))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
    }

    @Test
    void shouldCleanupKrrAfterVerifyFailure() {
        when(krrClient.getContactInformation(eq(RUN_ID), any()))
                .thenReturn(
                        Mono.just(KrrResourceStatus.emptyStatus()),
                        Mono.error(new IllegalStateException("Verification failed.")),
                        Mono.just(KrrResourceStatus.emptyStatus()));
        when(krrClient.createContactInformation(eq(RUN_ID), any()))
                .thenReturn(Mono.empty());
        when(krrClient.deleteContactInformation(RUN_ID))
                .thenReturn(Mono.empty());
        var lifecycle = krrLifecycle();
        var context = context("krr", FunctionalTestEnvironment.GLOBAL);

        StepVerifier.create(lifecycle.preflight(context)
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
                                                .then(Mono.error(throwable))))))
                .expectErrorMatches(error -> error instanceof IllegalStateException
                        && error.getMessage().equals("Verification failed."))
                .verify();

        var calls = inOrder(krrClient);
        calls.verify(krrClient).getContactInformation(eq(RUN_ID), any());
        calls.verify(krrClient).createContactInformation(eq(RUN_ID), any());
        calls.verify(krrClient).getContactInformation(eq(RUN_ID), any());
        calls.verify(krrClient).deleteContactInformation(RUN_ID);
        calls.verify(krrClient).getContactInformation(eq(RUN_ID), any());
    }

    private KrrFunctionalTest krrLifecycle() {
        return new KrrFunctionalTest(
                krrClient,
                pdlProperties,
                new KrrFunctionalTestProperties(),
                scheduler);
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
