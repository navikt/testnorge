package no.nav.registre.testnorge.personexportapi.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import no.nav.testnav.libs.slack.consumer.SlackConsumer;

@Component
public class SlackAdapter {
    private final SlackConsumer slackConsumer;
    private final String channel;

    public SlackAdapter(SlackConsumer slackConsumer, @Value("${consumers.slack.channel}") String channel) {
        this.slackConsumer = slackConsumer;
        this.channel = channel;
    }

    public void uploadFile(byte[] file, String fileName) {
        slackConsumer.uploadFile(file, fileName, channel);
    }
}
