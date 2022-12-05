package no.nav.testnav.libs.slack.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

import no.nav.testnav.libs.slack.command.PublishMessageCommand;
import no.nav.testnav.libs.slack.command.UploadFileCommand;
import no.nav.testnav.libs.slack.dto.Message;
import no.nav.testnav.libs.slack.dto.SlackResponse;

@Slf4j
public class SlackConsumer {
    private final String token;
    private final String applicationName;
    private final WebClient webClient;

    public SlackConsumer(
            String token,
            String baseUrl,
            String proxyHost,
            String applicationName
    ) {
        this.token = token;
        this.applicationName = applicationName;
        var builder = WebClient.builder();
        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Slack api", proxyHost);
            var uri = URI.create(proxyHost);
            builder.clientConnector(new ReactorClientHttpConnector(
                HttpClient
                    .create()
                    .proxy(proxy -> proxy
                        .type(ProxyProvider.Proxy.HTTP)
                        .host(uri.getHost())
                        .port(uri.getPort()))
            ));
        }
        webClient = builder
                .baseUrl(baseUrl)
                .build();
    }

    public void publish(Message message) {
        log.info("Publiserer melding til slack.");
        SlackResponse response = new PublishMessageCommand(webClient, token, message).call();
        if (!response.getOk()) {
            throw new SlackConsumerException("Klarer ikke aa opprette slack melding", response);
        }
    }

    public void uploadFile(byte[] file, String fileName, String channel) {
        log.info("Publiserer fil til slack.");
        SlackResponse response = new UploadFileCommand(webClient, token, file, fileName, channel, applicationName).call();
        if (!response.getOk()) {
            throw new SlackConsumerException("Klarer ikke aa opprette slack melding", response);
        }
    }

    private static class SlackConsumerException extends RuntimeException {
        public SlackConsumerException(String message, SlackResponse response) {
            super(message + " ( error: " + response.getError() + " )");
        }
    }

}
