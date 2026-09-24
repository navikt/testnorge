package no.nav.testnav.apps.statusfrontend.functionaltest;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusDescriptor;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalStatusCoordinatorTest {

    @Test
    void shouldRegisterSuccessfulTechnicalOnlyStatus() {
        var coordinator = coordinator(Mono.empty());

        var runId = coordinator.startSystem(new SystemId("technical-system"))
                .block(Duration.ofSeconds(1))
                .runId();
        var run = awaitCompleted(coordinator, runId);

        assertThat(run.results()).singleElement().satisfies(status -> {
            assertThat(status.state()).isEqualTo(FunctionalTestState.TECHNICAL_ONLY);
            assertThat(status.technicalStatus().state()).isEqualTo(TechnicalStatusState.UP);
        });
    }

    @Test
    void shouldRegisterFailedTechnicalOnlyStatusAsDown() {
        var coordinator = coordinator(Mono.error(new IllegalStateException("Unavailable.")));

        var runId = coordinator.startSystem(new SystemId("technical-system"))
                .block(Duration.ofSeconds(1))
                .runId();
        var run = awaitCompleted(coordinator, runId);

        assertThat(run.results()).singleElement().satisfies(status -> {
            assertThat(status.state()).isEqualTo(FunctionalTestState.TECHNICAL_ONLY);
            assertThat(status.technicalStatus().state()).isEqualTo(TechnicalStatusState.DOWN);
        });
    }

    private static FunctionalTestCoordinator coordinator(Mono<Void> result) {
        var definition = new TechnicalStatusDefinition() {
            @Override
            public TechnicalStatusDescriptor descriptor() {
                return new TechnicalStatusDescriptor(
                        new SystemId("technical-system"),
                        new DisplayName("Technical system"),
                        Set.of(FunctionalTestEnvironment.GLOBAL));
            }

            @Override
            public Mono<Void> check(FunctionalTestContext context) {
                return result;
            }
        };
        var clock = Clock.fixed(Instant.parse("2026-09-21T10:00:00Z"), ZoneOffset.UTC);
        return new FunctionalTestCoordinator(
                new FunctionalTestRegistry(List.of(), List.of(definition)),
                new FunctionalTestCache(clock),
                clock);
    }

    private static no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus awaitCompleted(
            FunctionalTestCoordinator coordinator,
            RunId runId
    ) {
        var deadline = System.nanoTime() + Duration.ofSeconds(2).toNanos();
        while (System.nanoTime() < deadline) {
            var run = coordinator.getRun(runId).block(Duration.ofSeconds(1));
            if (run != null && run.state() == FunctionalTestRunState.COMPLETED) {
                return run;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new AssertionError("Interrupted while waiting for test run.", exception);
            }
        }
        throw new AssertionError("Test run did not complete.");
    }
}
