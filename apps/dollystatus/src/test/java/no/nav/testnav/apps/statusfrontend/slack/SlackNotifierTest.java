package no.nav.testnav.apps.statusfrontend.slack;

import no.nav.testnav.apps.statusfrontend.config.SlackProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusState;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import no.nav.testnav.libs.slack.dto.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SlackNotifierTest {

    private static final String IDENT = "03458537037";

    @Mock
    private SlackConsumer slackConsumer;

    private SlackNotifier notifier;

    @BeforeEach
    void setUp() {
        notifier = new SlackNotifier(
                slackConsumer,
                new SlackTransitionTracker(),
                properties());
    }

    @AfterEach
    void tearDown() {
        notifier.stopNotifications();
    }

    @Test
    void shouldPublishSanitizedRedNotification() {
        notifier.onCompleted(failedStatus());

        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(slackConsumer, timeout(1000)).publish(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getChannel()).isEqualTo("C123");
        assertThat(messageCaptor.getValue().toString())
                .contains("DOWNSTREAM_SERVER", "aaf62d6f-eb87-49ce-bcef-b82ca3fd940d")
                .doesNotContain(IDENT, "Downstream response");
    }

    @Test
    void shouldNotPropagateSlackFailure() {
        doThrow(new IllegalStateException("Slack unavailable."))
                .when(slackConsumer)
                .publish(org.mockito.ArgumentMatchers.any());

        notifier.onCompleted(failedStatus());
        verify(slackConsumer, timeout(1000)).publish(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldNotPublishIndividualRecoveryOrRepeatedFailure() {
        notifier.onCompleted(failedStatus());
        notifier.onCompleted(failedStatus());
        notifier.onCompleted(status(FunctionalTestState.OK, TechnicalStatusState.UNKNOWN));

        verify(slackConsumer, timeout(1000)).publish(org.mockito.ArgumentMatchers.any());
        verifyNoMoreInteractions(slackConsumer);
    }

    @Test
    void shouldPublishOneGreenMessageForSuccessfulFullRun() {
        notifier.onFullRunCompleted(completedRun(List.of(
                status(FunctionalTestState.OK, TechnicalStatusState.UNKNOWN),
                status(FunctionalTestState.TECHNICAL_ONLY, TechnicalStatusState.UP))));

        var captor = ArgumentCaptor.forClass(Message.class);
        verify(slackConsumer, timeout(1000)).publish(captor.capture());
        assertThat(captor.getValue().toString())
                .contains(":large_green_circle: Alle dollystatus tester kjørte vellykket!")
                .doesNotContain(IDENT, "Downstream response");
        verifyNoMoreInteractions(slackConsumer);
    }

    @ParameterizedTest
    @EnumSource(value = FunctionalTestState.class, mode = EnumSource.Mode.EXCLUDE, names = "OK")
    void shouldNotPublishGreenMessageForIncompleteOrFailedRun(FunctionalTestState state) {
        notifier.onFullRunCompleted(completedRun(List.of(
                status(FunctionalTestState.OK, TechnicalStatusState.UNKNOWN),
                status(state, TechnicalStatusState.UNKNOWN))));

        verifyNoInteractions(slackConsumer);
    }

    @Test
    void shouldNotPublishGreenMessageForEmptyRunOrFailedTechnicalCheck() {
        notifier.onFullRunCompleted(completedRun(List.of()));
        notifier.onFullRunCompleted(completedRun(List.of(
                status(FunctionalTestState.TECHNICAL_ONLY, TechnicalStatusState.DOWN))));
        var completed = completedRun(List.of(status(FunctionalTestState.OK, TechnicalStatusState.UNKNOWN)));
        notifier.onFullRunCompleted(new FunctionalTestRunStatus(
                completed.runId(), FunctionalTestRunState.RUNNING, completed.startedAt(), null, completed.results()));

        verifyNoInteractions(slackConsumer);
    }

    @Test
    void shouldPublishOutsideReactorEventLoop() throws InterruptedException {
        var published = new CountDownLatch(1);
        var nonBlockingThread = new AtomicBoolean(true);
        doAnswer(_ -> {
            nonBlockingThread.set(Schedulers.isInNonBlockingThread());
            Mono.delay(Duration.ofMillis(1)).block(Duration.ofSeconds(1));
            published.countDown();
            return null;
        }).when(slackConsumer).publish(org.mockito.ArgumentMatchers.any());

        Mono.fromRunnable(() -> notifier.onCompleted(failedStatus()))
                .subscribeOn(Schedulers.parallel())
                .block(Duration.ofSeconds(1));

        assertThat(published.await(1, TimeUnit.SECONDS)).isTrue();
        assertThat(nonBlockingThread).isFalse();
    }

    private static FunctionalTestRunStatus completedRun(List<FunctionalTestStatus> results) {
        var status = failedStatus();
        return new FunctionalTestRunStatus(
                status.runId(), FunctionalTestRunState.COMPLETED, status.startedAt(), status.completedAt(), results);
    }

    private static SlackProperties properties() {
        var properties = new SlackProperties();
        properties.setChannel("C123");
        properties.setStatusPageUrl("https://dollystatus.intern.dev.nav.no/");
        return properties;
    }

    private static FunctionalTestStatus failedStatus() {
        return status(FunctionalTestState.CREATE_FAILED, TechnicalStatusState.UNKNOWN);
    }

    private static FunctionalTestStatus status(FunctionalTestState state, TechnicalStatusState technicalState) {
        var completedAt = Instant.parse("2026-09-21T10:00:00Z");
        return new FunctionalTestStatus(
                new SystemId("arena"),
                new DisplayName("Arena"),
                FunctionalTestEnvironment.Q1,
                RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d"),
                state,
                completedAt.minusSeconds(30),
                completedAt,
                completedAt.plusSeconds(3600),
                0,
                new FunctionalTestError(
                        FunctionalTestErrorCategory.DOWNSTREAM_SERVER,
                        "Downstream response " + IDENT),
                new TechnicalStatus(technicalState, completedAt));
    }
}
