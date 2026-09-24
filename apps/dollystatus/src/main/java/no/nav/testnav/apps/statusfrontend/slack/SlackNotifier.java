package no.nav.testnav.apps.statusfrontend.slack;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.config.SlackProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestResultListener;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import no.nav.testnav.libs.slack.dto.Message;
import no.nav.testnav.libs.slack.dto.Section;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "slack", name = "enabled", havingValue = "true")
public class SlackNotifier implements FunctionalTestResultListener {

    private final SlackConsumer slackConsumer;
    private final SlackTransitionTracker transitionTracker;
    private final SlackProperties properties;

    public SlackNotifier(
            SlackConsumer slackConsumer,
            SlackTransitionTracker transitionTracker,
            SlackProperties properties
    ) {
        this.slackConsumer = slackConsumer;
        this.transitionTracker = transitionTracker;
        this.properties = properties;
    }

    @Override
    public void onCompleted(FunctionalTestStatus status) {
        transitionTracker.update(status)
                .ifPresent(transition -> publish(status, transition));
    }

    private void publish(
            FunctionalTestStatus status,
            SlackTransitionTracker.SlackTransition transition
    ) {
        var text = transition == SlackTransitionTracker.SlackTransition.RED
                ? redMessage(status)
                : recoveryMessage(status);
        try {
            slackConsumer.publish(Message.builder()
                    .channel(properties.getChannel())
                    .blocks(List.of(Section.from(text)))
                    .attachments(List.of())
                    .build());
        } catch (RuntimeException exception) {
            log.error("Klarte ikke å sende statusvarsel til Slack: {}", exception.getClass().getSimpleName());
        }
    }

    private String redMessage(FunctionalTestStatus status) {
        var category = status.error() == null
                ? "TEKNISK_STATUS"
                : status.error().category().name();
        return ":red_circle: *%s (%s) er rød*\nFeilkategori: %s\nTid: %s\nKjøring: %s\n<%s|Åpne Dollystatus>"
                .formatted(
                        status.displayName().value(),
                        status.environment(),
                        category,
                        status.completedAt(),
                        status.runId().value(),
                        properties.getStatusPageUrl());
    }

    private String recoveryMessage(FunctionalTestStatus status) {
        return ":large_green_circle: *%s (%s) er grønn igjen*\nTid: %s\nKjøring: %s\n<%s|Åpne Dollystatus>"
                .formatted(
                        status.displayName().value(),
                        status.environment(),
                        status.completedAt(),
                        status.runId().value(),
                        properties.getStatusPageUrl());
    }
}
