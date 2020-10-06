package no.nav.registre.testnorge.tilbakemeldingapi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.libs.slack.consumer.SlackConsumer;
import no.nav.registre.testnorge.tilbakemeldingapi.domain.Tilbakemelding;

@Service
public class TilbakemeldingService {
    private final SlackConsumer slackConsumer;
    private final String channel;

    public TilbakemeldingService(
            SlackConsumer slackConsumer,
            @Value("${consumer.slack.channel}") String channel
    ) {
        this.slackConsumer = slackConsumer;
        this.channel = channel;
    }

    public void publish(Tilbakemelding tilbakemelding) {
        slackConsumer.publish(tilbakemelding.toSlackMessage(channel));
    }

}
