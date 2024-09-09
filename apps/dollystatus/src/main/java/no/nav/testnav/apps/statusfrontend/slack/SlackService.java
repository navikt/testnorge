package no.nav.testnav.apps.statusfrontend.slack;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.Message;
import com.slack.api.model.ResponseMetadata;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
class SlackService {

    private static final Instant START_TIME = Instant.now();
    private static final int MESSAGES_PER_PAGE = 100;

    final Slack slack = Slack.getInstance();
    private final String botToken;
    private final String channelId;

    final Set<String> activeAlertKeys = new HashSet<>();

    Summary getCurrentAlerts() {

        List<Alert> latestAlerts = new ArrayList<>();
        var cursor = "";

        do {
            try {

                var currentCursor = cursor;
                var response = slack
                        .methods(botToken)
                        .conversationsHistory(request -> request
                                .channel(channelId)
                                .oldest(String.valueOf(START_TIME.getEpochSecond()))
                                .limit(MESSAGES_PER_PAGE)
                                .cursor(currentCursor)
                        );
                if (!response.isOk()) {
                    throw new IOException("Error fetching messages: " + response.getError());
                }

                var newAlerts = response
                        .getMessages()
                        .stream()
                        .map(Alert::new)
                        .toList();
                latestAlerts.addAll(newAlerts);

                cursor = Optional
                        .ofNullable(response.getResponseMetadata())
                        .map(ResponseMetadata::getNextCursor)
                        .orElse("");

            } catch (IOException | SlackApiException e) {
                return new Summary(e.getMessage(), 1, Collections.emptyList());
            }
        } while (!cursor.isEmpty());

        latestAlerts = latestAlerts
                .stream()
                .sorted(Comparator.comparing(SlackService.Alert::getTimestamp))
                .toList();
        latestAlerts
                .forEach(alert -> {
                    if (alert.getMessage().contains("[FIRING")) {
                        activeAlertKeys.add(getAlertName(alert.getMessage()));
                    } else if (alert.getMessage().contains("[RESOLVED")) {
                        activeAlertKeys.remove(getAlertName(alert.getMessage()));
                    }
                });
        log.info("Current active {} alerts: {}", activeAlertKeys.size(), activeAlertKeys);
        return new Summary("OK", activeAlertKeys.size(), latestAlerts);

    }

    static String getAlertName(String message) {
        return message.substring(message.indexOf("]") + 2);
    }

    @Getter(AccessLevel.PACKAGE)
    static class Alert {

        @JsonProperty
        private final String message;


        @JsonProperty
        private final Instant timestamp;

        Alert(Message message) {
            var attachments = message.getAttachments();
            if (attachments != null && !attachments.isEmpty()) {
                this.message = attachments
                        .getFirst()
                        .getTitle();
            } else {
                this.message = null;
            }
            double ts = Double.parseDouble(message.getTs());
            long seconds = (long) ts;
            int nanos = (int) ((ts - seconds) * 1_000_000_000);
            this.timestamp = Instant.ofEpochSecond(seconds, nanos);
        }

    }

    record Summary(
            @JsonProperty String message,
            @JsonProperty int active,
            @JsonProperty List<Alert> alerts
    ) {
    }

}
