package no.nav.tpsidenter.vedlikehold.consumers;

import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
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
