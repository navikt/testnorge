package no.nav.registre.testnorge.rapportering.consumer;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.testnorge.rapportering.consumer.command.PublishMessageCommand;
import no.nav.registre.testnorge.rapportering.domain.Report;

@Component
public class SlackConsumer {
    private final String token;
    private final String url;
    private final String channel;
    private final RestTemplate restTemplate;

    public SlackConsumer(
            @Value("${consumer.slack.token}") String token,
            @Value("${consumer.slack.url}") String url,
            @Value("${consumer.slack.channel}") String channel,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.token = token;
        this.url = url;
        this.channel = channel;
        this.restTemplate = restTemplateBuilder.build();
    }

    @SneakyThrows
    public void publish(Report report) {
        var command = new PublishMessageCommand(token, url, restTemplate, report.toSlackMessage(channel));
        command.call();
    }
}
