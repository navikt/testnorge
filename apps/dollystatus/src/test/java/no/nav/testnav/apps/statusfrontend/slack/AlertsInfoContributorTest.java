package no.nav.testnav.apps.statusfrontend.slack;

import com.slack.api.model.Attachment;
import com.slack.api.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.info.Info;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AlertsInfoContributorTest {

    @Mock
    private SlackService slackServiceMock;

    @InjectMocks
    private AlertsInfoContributor alertsInfoContributor;

    @Test
    void shouldAddSlackAlertsToInfoBuilder() {

        var message1 = new Message();
        message1.setTs(String.valueOf(Instant.now().getEpochSecond()));
        message1.setAttachments(List.of(Attachment.builder().title("Test alert 1").build()));
        var message2 = new Message();
        message2.setTs(String.valueOf(Instant.now().getEpochSecond()));
        message2.setAttachments(List.of(Attachment.builder().title("Test alert 2").build()));
        var alerts = List.of(
                new SlackService.Alert(message1),
                new SlackService.Alert(message2));

        var slackServiceSummary = new SlackService.Summary("OK", 2, alerts);
        when(slackServiceMock.getCurrentAlerts()).thenReturn(slackServiceSummary);

        var builder = new Info.Builder();
        alertsInfoContributor.contribute(builder);
        var info = builder.build();
        log.info("INFO: {}", info.toString());
        verify(slackServiceMock, times(1)).getCurrentAlerts();

    }
}
