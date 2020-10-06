package no.nav.registre.testnorge.libs.slack.consumer;

import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.slack.command.PublishMessageCommand;
import no.nav.registre.testnorge.libs.slack.dto.Message;
import no.nav.registre.testnorge.libs.slack.dto.SlackResponse;

public class SlackConsumer {
    private final String token;
    private final WebClient webClient;

    public SlackConsumer(
            String token,
            String baseUrl
    ) {
        this.token = token;
        webClient = WebClient
                .builder()
                .baseUrl(baseUrl)
                .build();
    }

    public void publish(Message message) {
        SlackResponse response = new PublishMessageCommand(webClient, token, message).call();
        if (!response.getOk()) {
            throw new RuntimeException("Klarer ikke aa opprette slack melding");
        }
    }
}
