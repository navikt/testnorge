package no.nav.testnav.apps.statusfrontend.fagsystem;

import java.time.Instant;
import java.time.Duration;
import java.time.LocalDate;
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
import no.nav.testnav.apps.statusfrontend.fagsystem.nom.NomRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skattekort.SkattekortResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestExistingDataException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestVerificationTimeoutException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.libs.dto.kontoregister.v1.OppdaterKontoRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
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
import static org.mockito.Mockito.never;
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
    void shouldCleanupExistingArenaUserBeforeNewPreflight() {
        when(arenaClient.getUser(eq(FunctionalTestEnvironment.Q1), eq(RUN_ID), any()))
                .thenReturn(Mono.just(new ArenaResourceStatus(false, true, true, false)),
                        Mono.just(new ArenaResourceStatus(false, false, false, true)));
        when(arenaClient.deactivateUser(FunctionalTestEnvironment.Q1, RUN_ID)).thenReturn(Mono.empty());
        var lifecycle = new ArenaFunctionalTest(
                arenaClient,
                pdlProperties,
                new ArenaFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(context("arena", FunctionalTestEnvironment.Q1)))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        var context = context("arena", FunctionalTestEnvironment.Q1);
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();
        verify(arenaClient).deactivateUser(FunctionalTestEnvironment.Q1, RUN_ID);
    }

    @Test
    void shouldCleanupExistingKontoregisterAccountBeforeNewPreflight() {
        when(kontoregisterClient.getAccount(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new KontoregisterResourceStatus(false, false)),
                        Mono.just(KontoregisterResourceStatus.emptyStatus()));
        when(kontoregisterClient.deleteAccount(RUN_ID)).thenReturn(Mono.empty());
        var lifecycle = new KontoregisterFunctionalTest(
                kontoregisterClient,
                pdlProperties,
                new KontoregisterFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(
                        context("kontoregister", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        var context = context("kontoregister", FunctionalTestEnvironment.GLOBAL);
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();
        verify(kontoregisterClient).deleteAccount(RUN_ID);
    }

    @Test
    void shouldCleanupExistingKrrContactInformationBeforeNewPreflight() {
        when(krrClient.getContactInformation(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new KrrResourceStatus(false, false)),
                        Mono.just(KrrResourceStatus.emptyStatus()));
        when(krrClient.deleteContactInformation(RUN_ID)).thenReturn(Mono.empty());
        var lifecycle = krrLifecycle();

        StepVerifier.create(lifecycle.preflight(context("krr", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        var context = context("krr", FunctionalTestEnvironment.GLOBAL);
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();
        verify(krrClient).deleteContactInformation(RUN_ID);
    }

    @Test
    void shouldCleanupExistingNomResourceWithRetainedDateBeforeNewPreflight() {
        var originalStart = LocalDate.of(2020, 1, 1);
        var endDate = LocalDate.of(2026, 9, 20);
        var existing = new NomResourceStatus(false, false, false, "12345", originalStart, null);
        var closed = new NomResourceStatus(false, false, true, "12345", originalStart, endDate);
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(existing), Mono.just(existing), Mono.just(closed));
        when(nomClient.closeResource(RUN_ID, endDate)).thenReturn(Mono.empty());
        var lifecycle = new NomFunctionalTest(
                nomClient,
                pdlProperties,
                new NomFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(context("nom", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        var context = context("nom", FunctionalTestEnvironment.GLOBAL);
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .assertNext(preflight -> assertThat(preflight.previousStatus()).isEqualTo(closed))
                .verifyComplete();
        var calls = inOrder(nomClient);
        calls.verify(nomClient, org.mockito.Mockito.times(2)).getResource(eq(RUN_ID), any());
        calls.verify(nomClient).closeResource(RUN_ID, endDate);
        calls.verify(nomClient, org.mockito.Mockito.times(2)).getResource(eq(RUN_ID), any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345", "other-resource"})
    void shouldRequireSameNomResourceIdAfterCleaningExistingData(String returnedResourceId) {
        var endDate = LocalDate.of(2026, 9, 20);
        var originalStart = LocalDate.of(2020, 1, 1);
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new NomResourceStatus(false, false, false, "12345", originalStart, null)),
                        Mono.just(new NomResourceStatus(false, false, true,
                                returnedResourceId, originalStart, endDate)));
        when(nomClient.closeResource(RUN_ID, endDate)).thenReturn(Mono.empty());
        var lifecycle = nomLifecycle();
        var verification = StepVerifier.withVirtualTime(
                () -> lifecycle.cleanupExistingData(context("nom", FunctionalTestEnvironment.GLOBAL)),
                () -> scheduler, 1);
        if (returnedResourceId.equals("12345")) {
            verification.verifyComplete();
        } else {
            verification.thenAwait(Duration.ofMinutes(3))
                    .expectError(FunctionalTestVerificationTimeoutException.class).verify();
        }
    }

    @Test
    void shouldNotCloseExistingNomResourceWithoutId() {
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new NomResourceStatus(false, false, false, null,
                        LocalDate.of(2020, 1, 1), null)));

        StepVerifier.create(nomLifecycle().cleanupExistingData(context("nom", FunctionalTestEnvironment.GLOBAL)))
                .expectError(IllegalStateException.class).verify();
        verify(nomClient, never()).closeResource(any(), any());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void shouldRequestCleanupOfNomWhenExistingEndDateHasNotPassed(int daysUntilEnd) {
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new NomResourceStatus(
                        false, false, true, "12345",
                        LocalDate.of(2026, 9, 19),
                        LocalDate.of(2026, 9, 21).plusDays(daysUntilEnd))));

        StepVerifier.create(nomLifecycle().preflight(context("nom", FunctionalTestEnvironment.GLOBAL)))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        verify(nomClient, never()).createResource(any(), any());
        verify(nomClient, never()).closeResource(any(), any());
    }

    @Test
    void shouldCreateAndCloseNomWithDatesThatAllowAnotherRunTheSameDay() {
        var endDate = LocalDate.of(2026, 9, 20);
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(
                        Mono.just(NomResourceStatus.emptyStatus()),
                        Mono.just(new NomResourceStatus(false, true, false, "12345",
                                LocalDate.of(2026, 9, 19), null)),
                        Mono.just(new NomResourceStatus(false, true, true, "12345",
                                LocalDate.of(2026, 9, 19), endDate)));
        when(nomClient.createResource(eq(RUN_ID), any())).thenReturn(Mono.empty());
        when(nomClient.closeResource(RUN_ID, endDate)).thenReturn(Mono.empty());
        var lifecycle = nomLifecycle();
        var context = context("nom", FunctionalTestEnvironment.GLOBAL);

        StepVerifier.create(lifecycle.preflight(context)
                        .flatMap(preflight -> lifecycle.create(context, preflight)
                                .flatMap(created -> lifecycle.verify(context, preflight, created)
                                        .flatMap(verified -> lifecycle.cleanup(
                                                context, preflight, Optional.of(created), Optional.of(verified),
                                                lifecycle.descriptor().expectedCleanupState()))))
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .assertNext(preflight -> assertThat(preflight.previousStatus().endDate()).isEqualTo(endDate))
                .verifyComplete();

        var requestCaptor = ArgumentCaptor.forClass(NomRequest.class);
        verify(nomClient).createResource(eq(RUN_ID), requestCaptor.capture());
        assertThat(requestCaptor.getValue().startDato()).isEqualTo(LocalDate.of(2026, 9, 19));
        verify(nomClient).closeResource(RUN_ID, endDate);
    }

    @Test
    void shouldDiscoverNomResourceIdForCleanupAfterVerificationFailure() {
        var endDate = LocalDate.of(2026, 9, 20);
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(
                        Mono.just(new NomResourceStatus(false, true, false, "12345",
                                LocalDate.of(2026, 9, 19), null)),
                        Mono.just(new NomResourceStatus(false, true, true, "12345",
                                LocalDate.of(2026, 9, 19), endDate)));
        when(nomClient.closeResource(RUN_ID, endDate)).thenReturn(Mono.empty());
        var lifecycle = nomLifecycle();

        StepVerifier.create(lifecycle.cleanup(
                        context("nom", FunctionalTestEnvironment.GLOBAL),
                        new NomFunctionalTest.Preflight(NomResourceStatus.emptyStatus()),
                        Optional.of(Creation.COMPLETED), Optional.empty(),
                        lifecycle.descriptor().expectedCleanupState()))
                .verifyComplete();

        var calls = inOrder(nomClient);
        calls.verify(nomClient).getResource(eq(RUN_ID), any());
        calls.verify(nomClient).closeResource(RUN_ID, endDate);
        calls.verify(nomClient).getResource(eq(RUN_ID), any());
    }

    @Test
    void shouldSkipNomCleanupWhenFailedCreationLeftNoResource() {
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(NomResourceStatus.emptyStatus()));
        var lifecycle = nomLifecycle();

        StepVerifier.create(lifecycle.cleanup(
                        context("nom", FunctionalTestEnvironment.GLOBAL),
                        new NomFunctionalTest.Preflight(NomResourceStatus.emptyStatus()),
                        Optional.empty(), Optional.empty(), lifecycle.descriptor().expectedCleanupState()))
                .verifyComplete();
        verify(nomClient, never()).closeResource(any(), any());
    }

    @Test
    void shouldNotCloseUnexpectedNomResourceAfterFailedCreation() {
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new NomResourceStatus(false, false, false, "54321",
                        LocalDate.of(2026, 9, 19), null)));
        var lifecycle = nomLifecycle();

        StepVerifier.create(lifecycle.cleanup(
                        context("nom", FunctionalTestEnvironment.GLOBAL),
                        new NomFunctionalTest.Preflight(NomResourceStatus.emptyStatus()),
                        Optional.empty(), Optional.empty(), lifecycle.descriptor().expectedCleanupState()))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
        verify(nomClient, never()).closeResource(any(), any());
    }

    @Test
    void shouldCleanupExistingSkattekortBeforeNewPreflight() {
        when(skattekortClient.getTaxCard(
                FunctionalTestEnvironment.Q2,
                RUN_ID,
                2026)).thenReturn(Mono.just(new SkattekortResourceStatus(false, true, false)),
                Mono.just(new SkattekortResourceStatus(false, false, true)));
        when(skattekortClient.createTaxCard(eq(FunctionalTestEnvironment.Q2), eq(RUN_ID), any()))
                .thenReturn(Mono.empty());
        var lifecycle = new SkattekortFunctionalTest(
                skattekortClient,
                pdlProperties,
                new SkattekortFunctionalTestProperties(),
                scheduler);

        StepVerifier.create(lifecycle.preflight(
                        context("skattekort", FunctionalTestEnvironment.Q2)))
                .expectError(FunctionalTestExistingDataException.class)
                .verify();
        var context = context("skattekort", FunctionalTestEnvironment.Q2);
        StepVerifier.create(lifecycle.cleanupExistingData(context)
                        .then(Mono.defer(() -> lifecycle.preflight(context))))
                .expectNextCount(1).verifyComplete();
        verify(skattekortClient).createTaxCard(eq(FunctionalTestEnvironment.Q2), eq(RUN_ID), any());
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldVerifyAndCleanupReactivatedNomWithItsOriginalStartDate(boolean verificationFailed) {
        var originalStart = LocalDate.of(2026, 9, 20);
        var previousStatus = new NomResourceStatus(false, true, true, "12345", originalStart, originalStart);
        var reactivatedStatus = new NomResourceStatus(false, true, false, "12345", originalStart, null);
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(previousStatus), Mono.just(reactivatedStatus),
                        Mono.just(new NomResourceStatus(false, true, true, "12345", originalStart, originalStart)));
        when(nomClient.createResource(eq(RUN_ID), any())).thenReturn(Mono.empty());
        when(nomClient.closeResource(RUN_ID, originalStart)).thenReturn(Mono.empty());
        var lifecycle = nomLifecycle();
        var context = context("nom", FunctionalTestEnvironment.GLOBAL);

        StepVerifier.create(lifecycle.preflight(context)
                        .flatMap(preflight -> lifecycle.create(context, preflight)
                                .flatMap(created -> verificationFailed
                                        ? lifecycle.cleanup(context, preflight, Optional.of(created), Optional.empty(),
                                                lifecycle.descriptor().expectedCleanupState())
                                        : lifecycle.verify(context, preflight, created)
                                                .flatMap(verified -> lifecycle.cleanup(
                                                        context, preflight, Optional.of(created), Optional.of(verified),
                                                        lifecycle.descriptor().expectedCleanupState())))))
                .verifyComplete();

        verify(nomClient).closeResource(RUN_ID, originalStart);
    }

    @ParameterizedTest
    @CsvSource({"12345,2026-09-18,true", "54321,2026-09-20,true", "12345,2026-09-20,false"})
    void shouldRejectUnexpectedNomDateResourceOrPerson(
            String resourceId, LocalDate actualStartDate, boolean expectedPersonPresent) {
        var originalStart = LocalDate.of(2026, 9, 20);
        var preflight = new NomFunctionalTest.Preflight(
                new NomResourceStatus(false, true, true, "12345", originalStart, originalStart));
        when(nomClient.getResource(eq(RUN_ID), any()))
                .thenReturn(Mono.just(new NomResourceStatus(
                        false, expectedPersonPresent, false, resourceId, actualStartDate, null)));
        var lifecycle = nomLifecycle();
        var context = context("nom", FunctionalTestEnvironment.GLOBAL);

        StepVerifier.withVirtualTime(
                        () -> lifecycle.verify(context, preflight, Creation.COMPLETED),
                        () -> scheduler, 1)
                .thenAwait(Duration.ofMinutes(3))
                .expectError(FunctionalTestVerificationTimeoutException.class)
                .verify();
        StepVerifier.create(lifecycle.cleanup(context, preflight, Optional.of(Creation.COMPLETED),
                        Optional.empty(), lifecycle.descriptor().expectedCleanupState()))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
        verify(nomClient, never()).closeResource(any(), any());
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

    private NomFunctionalTest nomLifecycle() {
        return new NomFunctionalTest(nomClient, pdlProperties, new NomFunctionalTestProperties(), scheduler);
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
