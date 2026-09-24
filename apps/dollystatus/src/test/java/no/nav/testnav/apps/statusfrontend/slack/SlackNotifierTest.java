package no.nav.testnav.apps.statusfrontend.slack;

import no.nav.testnav.apps.statusfrontend.config.SlackProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestError;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestErrorCategory;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatus;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import no.nav.testnav.libs.slack.dto.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SlackNotifierTest {

    private static final String IDENT = "03458537037";

    @Mock
    private SlackConsumer slackConsumer;

    @Test
    void shouldPublishSanitizedRedNotification() {
        var properties = properties();
        var notifier = new SlackNotifier(
                slackConsumer,
                new SlackTransitionTracker(),
                properties);

        notifier.onCompleted(failedStatus());

        var messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(slackConsumer).publish(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getChannel()).isEqualTo("C123");
        assertThat(messageCaptor.getValue().toString())
                .contains("DOWNSTREAM_SERVER", "aaf62d6f-eb87-49ce-bcef-b82ca3fd940d")
                .doesNotContain(IDENT, "Downstream response");
    }

    @Test
    void shouldNotPropagateSlackFailure() {
        var notifier = new SlackNotifier(
                slackConsumer,
                new SlackTransitionTracker(),
                properties());
        doThrow(new IllegalStateException("Slack unavailable."))
                .when(slackConsumer)
                .publish(org.mockito.ArgumentMatchers.any());

        notifier.onCompleted(failedStatus());
    }

    private static SlackProperties properties() {
        var properties = new SlackProperties();
        properties.setChannel("C123");
        properties.setStatusPageUrl("https://dollystatus.intern.dev.nav.no/");
        return properties;
    }

    private static FunctionalTestStatus failedStatus() {
        var completedAt = Instant.parse("2026-09-21T10:00:00Z");
        return new FunctionalTestStatus(
                new SystemId("arena"),
                new DisplayName("Arena"),
                FunctionalTestEnvironment.Q1,
                RunId.from("aaf62d6f-eb87-49ce-bcef-b82ca3fd940d"),
                FunctionalTestState.CREATE_FAILED,
                completedAt.minusSeconds(30),
                completedAt,
                completedAt.plusSeconds(3600),
                0,
                new FunctionalTestError(
                        FunctionalTestErrorCategory.DOWNSTREAM_SERVER,
                        "Downstream response " + IDENT),
                TechnicalStatus.unknown());
    }
}
