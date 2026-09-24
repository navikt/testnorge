package no.nav.testnav.apps.statusfrontend.fagsystem;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.BrregstubFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.InntektstubFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.PdlFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SkjermingsregisterFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.UdiFunctionalTestProperties;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.brregstub.BrregstubResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.inntektstub.InntektstubRequest;
import no.nav.testnav.apps.statusfrontend.fagsystem.sigrun.SigrunTechnicalStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.sigrun.SigrunTechnicalStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.skjermingsregister.SkjermingsregisterResourceStatus;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiFunctionalTest;
import no.nav.testnav.apps.statusfrontend.fagsystem.udi.UdiResourceStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestBlockedException;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Creation;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Preflight;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.EmptyTestResult.Verification;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThirdBatchFunctionalTestLifecycleTest {

    private static final String IDENT = "03458537037";
    private static final Instant STARTED_AT = Instant.parse("2026-09-21T10:00:00Z");
    private static final RunId RUN_ID =
            RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d");

    @Mock
    private BrregstubClient brregstubClient;

    @Mock
    private InntektstubClient inntektstubClient;

    @Mock
    private SkjermingsregisterClient skjermingsregisterClient;

    @Mock
    private UdiClient udiClient;

    @Mock
    private SigrunTechnicalStatusClient sigrunTechnicalStatusClient;

    private PdlFunctionalTestProperties pdlProperties;
    private VirtualTimeScheduler scheduler;

    @BeforeEach
    void setUp() {
        pdlProperties = new PdlFunctionalTestProperties();
        pdlProperties.setIdent(IDENT);
        scheduler = VirtualTimeScheduler.create();
    }

    @Test
    void shouldExposeOneGlobalStatusAndRequirePdl() {
        var brregstub = brregstubLifecycle();
        var inntektstub = inntektstubLifecycle();
        var skjermingsregister = skjermingsregisterLifecycle();
        var udi = udiLifecycle();

        assertThat(brregstub.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.GLOBAL));
        assertThat(inntektstub.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.GLOBAL));
        assertThat(skjermingsregister.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.GLOBAL));
        assertThat(udi.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.GLOBAL));
        assertThat(brregstub.requiresPdl()).isTrue();
        assertThat(inntektstub.requiresPdl()).isTrue();
        assertThat(skjermingsregister.requiresPdl()).isTrue();
        assertThat(udi.requiresPdl()).isTrue();

        var sigrun = new SigrunTechnicalStatus(sigrunTechnicalStatusClient);
        assertThat(sigrun.descriptor().environments())
                .isEqualTo(Set.of(FunctionalTestEnvironment.GLOBAL));
        assertThat(sigrun.requiresPdl()).isFalse();
    }

    @Test
    void shouldBlockEveryUnsafeMutatingPreflight() {
        when(brregstubClient.getRoleOverview(any()))
                .thenReturn(Mono.just(new BrregstubResourceStatus(false, false)));
        when(brregstubClient.getOrganization(any()))
                .thenReturn(Mono.just(BrregstubResourceStatus.emptyStatus()));
        when(inntektstubClient.getIncome(any()))
                .thenReturn(Mono.just(new InntektstubResourceStatus(false, false)));
        when(skjermingsregisterClient.getScreening(any(), any()))
                .thenReturn(Mono.just(new SkjermingsregisterResourceStatus(
                        false,
                        false,
                        true,
                        false,
                        false)));
        when(udiClient.getPerson(any()))
                .thenReturn(Mono.just(new UdiResourceStatus(false, false)));

        StepVerifier.create(brregstubLifecycle().preflight(context("brregstub")))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
        StepVerifier.create(inntektstubLifecycle().preflight(context("inntektstub")))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
        StepVerifier.create(skjermingsregisterLifecycle().preflight(
                        context("skjermingsregister")))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
        StepVerifier.create(udiLifecycle().preflight(context("udi")))
                .expectError(FunctionalTestBlockedException.class)
                .verify();
    }

    @Test
    void shouldVerifyAndAfterCheckBrregstubAndUdiDeletes() {
        when(brregstubClient.getRoleOverview(any()))
                .thenReturn(
                        Mono.just(BrregstubResourceStatus.emptyStatus()),
                        Mono.just(new BrregstubResourceStatus(false, true)),
                        Mono.just(BrregstubResourceStatus.emptyStatus()));
        when(brregstubClient.getOrganization(any()))
                .thenReturn(
                        Mono.just(BrregstubResourceStatus.emptyStatus()),
                        Mono.just(new BrregstubResourceStatus(false, true)),
                        Mono.just(new BrregstubResourceStatus(false, true)),
                        Mono.just(BrregstubResourceStatus.emptyStatus()));
        when(brregstubClient.createRoleOverview(any())).thenReturn(Mono.empty());
        when(brregstubClient.deleteRoleOverview(IDENT)).thenReturn(Mono.empty());
        when(brregstubClient.deleteOrganization()).thenReturn(Mono.empty());
        var brregstub = brregstubLifecycle();
        var brregstubContext = context("brregstub");

        StepVerifier.create(brregstub.preflight(brregstubContext)
                        .flatMap(preflight -> brregstub.create(brregstubContext, preflight)
                                .flatMap(created -> brregstub.verify(
                                                brregstubContext,
                                                preflight,
                                                created)
                                        .flatMap(verified -> brregstub.cleanup(
                                                brregstubContext,
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                brregstub.descriptor()
                                                        .expectedCleanupState())))))
                .verifyComplete();

        var brregstubCalls = inOrder(brregstubClient);
        brregstubCalls.verify(brregstubClient).getRoleOverview(any());
        brregstubCalls.verify(brregstubClient).getOrganization(any());
        brregstubCalls.verify(brregstubClient).createRoleOverview(any());
        brregstubCalls.verify(brregstubClient).getRoleOverview(any());
        brregstubCalls.verify(brregstubClient).getOrganization(any());
        brregstubCalls.verify(brregstubClient).deleteRoleOverview(IDENT);
        brregstubCalls.verify(brregstubClient).getOrganization(any());
        brregstubCalls.verify(brregstubClient).deleteOrganization();
        brregstubCalls.verify(brregstubClient).getRoleOverview(any());
        brregstubCalls.verify(brregstubClient).getOrganization(any());

        when(udiClient.getPerson(any()))
                .thenReturn(
                        Mono.just(UdiResourceStatus.emptyStatus()),
                        Mono.just(new UdiResourceStatus(false, true)),
                        Mono.just(UdiResourceStatus.emptyStatus()));
        when(udiClient.createPerson(any())).thenReturn(Mono.empty());
        when(udiClient.deletePerson(IDENT)).thenReturn(Mono.empty());
        var udi = udiLifecycle();
        var udiContext = context("udi");

        StepVerifier.create(udi.preflight(udiContext)
                        .flatMap(preflight -> udi.create(udiContext, preflight)
                                .flatMap(created -> udi.verify(
                                                udiContext,
                                                preflight,
                                                created)
                                        .flatMap(verified -> udi.cleanup(
                                                udiContext,
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                udi.descriptor().expectedCleanupState())))))
                .verifyComplete();

        var udiCalls = inOrder(udiClient);
        udiCalls.verify(udiClient).getPerson(any());
        udiCalls.verify(udiClient).createPerson(any());
        udiCalls.verify(udiClient).getPerson(any());
        udiCalls.verify(udiClient).deletePerson(IDENT);
        udiCalls.verify(udiClient).getPerson(any());
    }

    @Test
    void shouldDeleteInntektstubOnlyAfterEmptyPreflightAndAfterCheck() {
        when(inntektstubClient.getIncome(any()))
                .thenReturn(
                        Mono.just(InntektstubResourceStatus.emptyStatus()),
                        Mono.just(new InntektstubResourceStatus(false, true)),
                        Mono.just(InntektstubResourceStatus.emptyStatus()));
        when(inntektstubClient.createIncome(any())).thenReturn(Mono.empty());
        when(inntektstubClient.deleteIncome(IDENT)).thenReturn(Mono.empty());
        var lifecycle = inntektstubLifecycle();
        var context = context("inntektstub");

        StepVerifier.create(lifecycle.preflight(context)
                        .flatMap(preflight -> lifecycle.create(context, preflight)
                                .flatMap(created -> lifecycle.verify(
                                                context,
                                                preflight,
                                                created)
                                        .flatMap(verified -> lifecycle.cleanup(
                                                context,
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                lifecycle.descriptor()
                                                        .expectedCleanupState())))))
                .verifyComplete();

        var calls = inOrder(inntektstubClient);
        calls.verify(inntektstubClient).getIncome(any());
        var requestCaptor = ArgumentCaptor.forClass(InntektstubRequest.class);
        calls.verify(inntektstubClient).createIncome(requestCaptor.capture());
        calls.verify(inntektstubClient).getIncome(any());
        calls.verify(inntektstubClient).deleteIncome(IDENT);
        calls.verify(inntektstubClient).getIncome(any());
        assertThat(requestCaptor.getValue().inntektsliste()).singleElement()
                .satisfies(income -> {
                    assertThat(income.inngaarIGrunnlagForTrekk()).isTrue();
                    assertThat(income.utloeserArbeidsgiveravgift()).isTrue();
                });
    }

    @Test
    void shouldNotDeleteBrregstubOrganizationWhenOwnershipNoLongerMatches() {
        when(brregstubClient.deleteRoleOverview(IDENT)).thenReturn(Mono.empty());
        when(brregstubClient.getOrganization(any()))
                .thenReturn(Mono.just(new BrregstubResourceStatus(false, false)));
        var lifecycle = brregstubLifecycle();

        StepVerifier.create(lifecycle.cleanup(
                        context("brregstub"),
                        Preflight.COMPLETED,
                        Optional.of(Creation.COMPLETED),
                        Optional.of(Verification.COMPLETED),
                        lifecycle.descriptor().expectedCleanupState()))
                .expectError(FunctionalTestBlockedException.class)
                .verify();

        verify(brregstubClient).deleteRoleOverview(IDENT);
        verify(brregstubClient).getOrganization(any());
        verify(brregstubClient, never()).deleteOrganization();
    }

    @Test
    void shouldVerifyTerminatedSkjermingInsteadOfAbsence() {
        when(skjermingsregisterClient.getScreening(any(), any()))
                .thenReturn(
                        Mono.just(SkjermingsregisterResourceStatus.emptyStatus()),
                        Mono.just(new SkjermingsregisterResourceStatus(
                                false,
                                true,
                                true,
                                false,
                                true)),
                        Mono.just(new SkjermingsregisterResourceStatus(
                                false,
                                true,
                                false,
                                true,
                                true)));
        when(skjermingsregisterClient.createScreening(any())).thenReturn(Mono.empty());
        when(skjermingsregisterClient.updateScreening(any())).thenReturn(Mono.empty());
        var lifecycle = skjermingsregisterLifecycle();
        var context = context("skjermingsregister");

        StepVerifier.create(lifecycle.preflight(context)
                        .flatMap(preflight -> lifecycle.create(context, preflight)
                                .flatMap(created -> lifecycle.verify(
                                                context,
                                                preflight,
                                                created)
                                        .flatMap(verified -> lifecycle.cleanup(
                                                context,
                                                preflight,
                                                Optional.of(created),
                                                Optional.of(verified),
                                                lifecycle.descriptor()
                                                        .expectedCleanupState())))))
                .verifyComplete();

        var calls = inOrder(skjermingsregisterClient);
        calls.verify(skjermingsregisterClient).getScreening(any(), any());
        calls.verify(skjermingsregisterClient).createScreening(any());
        calls.verify(skjermingsregisterClient).getScreening(any(), any());
        calls.verify(skjermingsregisterClient).updateScreening(any());
        calls.verify(skjermingsregisterClient).getScreening(any(), any());
    }

    private BrregstubFunctionalTest brregstubLifecycle() {
        return new BrregstubFunctionalTest(
                brregstubClient,
                pdlProperties,
                new BrregstubFunctionalTestProperties(),
                scheduler);
    }

    private InntektstubFunctionalTest inntektstubLifecycle() {
        return new InntektstubFunctionalTest(
                inntektstubClient,
                pdlProperties,
                new InntektstubFunctionalTestProperties(),
                scheduler);
    }

    private SkjermingsregisterFunctionalTest skjermingsregisterLifecycle() {
        return new SkjermingsregisterFunctionalTest(
                skjermingsregisterClient,
                pdlProperties,
                new SkjermingsregisterFunctionalTestProperties(),
                scheduler);
    }

    private UdiFunctionalTest udiLifecycle() {
        return new UdiFunctionalTest(
                udiClient,
                pdlProperties,
                new UdiFunctionalTestProperties(),
                scheduler);
    }

    private static FunctionalTestContext context(String systemId) {
        return new FunctionalTestContext(
                RUN_ID,
                new SystemId(systemId),
                FunctionalTestEnvironment.GLOBAL,
                STARTED_AT);
    }
}
