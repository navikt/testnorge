package no.nav.testnav.apps.statusfrontend.slack;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.config.SlackProperties;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestResultListener;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunState;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestRunStatus;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestStatus;
import no.nav.testnav.libs.slack.consumer.SlackConsumer;
import no.nav.testnav.libs.slack.dto.Message;
import no.nav.testnav.libs.slack.dto.Section;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "slack", name = "enabled", havingValue = "true")
public class SlackNotifier implements FunctionalTestResultListener {

    private final SlackConsumer slackConsumer;
    private final SlackTransitionTracker transitionTracker;
    private final SlackProperties properties;
    private final Scheduler notificationScheduler =
            Schedulers.newBoundedElastic(1, 256, "dollystatus-slack");

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
                .ifPresent(_ -> publish(redMessage(status)));
    }

    @Override
    public void onFullRunCompleted(FunctionalTestRunStatus run) {
        if (run.state() == FunctionalTestRunState.COMPLETED
                && !run.results().isEmpty()
                && run.results().stream().allMatch(SlackTransitionTracker::isSuccessful)) {
            publish(":large_green_circle: Alle dollystatus tester kjørte vellykket!");
        }
    }

    private void publish(String text) {
        var message = Message.builder()
                .channel(properties.getChannel())
                .blocks(List.of(Section.from(text)))
                .attachments(List.of())
                .build();
        Mono.fromRunnable(() -> slackConsumer.publish(message))
                .subscribeOn(notificationScheduler)
                .subscribe(
                        _ -> {
                        },
                        exception -> log.error(
                                "Klarte ikke å sende statusvarsel til Slack: {}",
                                exception.getClass().getSimpleName()));
    }

    @PreDestroy
    void stopNotifications() {
        notificationScheduler.dispose();
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
}
