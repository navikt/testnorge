package no.nav.testnav.apps.statusfrontend.slack;

import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest;
import com.slack.api.methods.response.conversations.ConversationsHistoryResponse;
import com.slack.api.model.Attachment;
import com.slack.api.model.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlackServiceTest {

    @Mock
    private com.slack.api.methods.MethodsClient methodsClient;

    @Mock
    private Slack slack;

    @Test
    void getCurrentAlerts_shouldReturnAlertsFromSlack()
            throws IOException, SlackApiException {

        var service = new SlackService(slack, "botToken", "channelId");

        var message1 = new Message();
        message1.setTs("1725879142.167919");
        message1.setAttachments(List.of(Attachment.builder().title("[FIRING:1] Test alert 1").build()));

        var message2 = new Message();
        message2.setTs("1725879143.167919");
        message2.setAttachments(List.of(Attachment.builder().title("[RESOLVED:0] Test alert 1").build()));

        var conversationsHistoryResponse = new ConversationsHistoryResponse();
        conversationsHistoryResponse.setOk(true);
        conversationsHistoryResponse.setMessages(List.of(message1, message2));

        var requestCaptor = ArgumentCaptor.forClass(ConversationsHistoryRequest.class);

        when(slack.methods(anyString()))
                .thenReturn(methodsClient);
        when(methodsClient.conversationsHistory(requestCaptor.capture()))
                .thenReturn(conversationsHistoryResponse);

        var summary = service.getCurrentAlerts();
        assertThat(summary.message())
                .isEqualTo("OK");
        assertThat(summary.active())
                .isZero();
        assertThat(summary.alerts())
                .hasSize(2);

        var capturedRequests = requestCaptor.getAllValues();
        assertThat(capturedRequests)
                .isNotEmpty();
        assertThat(capturedRequests.getFirst().getCursor())
                .isEmpty();

    }

    @Test
    void getCurrentAlerts_shouldHandleError()
            throws IOException, SlackApiException {

        var service = new SlackService(slack, "botToken", "channelId");

        var requestCaptor = ArgumentCaptor.forClass(ConversationsHistoryRequest.class);

        when(methodsClient.conversationsHistory(any(ConversationsHistoryRequest.class)))
                .thenThrow(IOException.class);
        when(service.slack.methods(anyString()))
                .thenReturn(methodsClient);

        var summary = service.getCurrentAlerts();
        assertThat(summary.active())
                .isEqualTo(1);
        assertThat(summary.alerts())
                .isEmpty();

        var capturedRequests = requestCaptor.getAllValues();
        assertThat(capturedRequests)
                .isEmpty();

    }

    @Test
    void getAlertName_shouldExtractAlertNameFromMessage() {
        var alertName = SlackService.getAlertName("[FIRING:1] This is the alert name");

        assertThat(alertName).isEqualTo("This is the alert name");
    }
}