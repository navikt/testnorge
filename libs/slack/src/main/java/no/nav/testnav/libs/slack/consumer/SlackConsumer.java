package no.nav.testnav.libs.slack.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.slack.command.PublishMessageCommand;
import no.nav.testnav.libs.slack.dto.Message;
import no.nav.testnav.libs.slack.dto.SlackResponse;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.net.URI;

@Slf4j
public class SlackConsumer {

    private final String token;
    private final WebClient webClient;

    public SlackConsumer(
            WebClient webClient,
            String token,
            String baseUrl,
            String proxyHost
    ) {
        this.token = token;

        var builder = webClient
                .mutate()
                .baseUrl(baseUrl);
        if (proxyHost != null) {
            log.trace("Setter opp proxy host {} for Slack api", proxyHost);
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
        this.webClient = builder.build();
    }

    public void publish(Message message) {
        log.info("Publiserer melding til slack.");
        SlackResponse response = new PublishMessageCommand(webClient, token, message).call();
        if (!Boolean.TRUE.equals(response.getOk())) {
            throw new SlackConsumerException("Klarer ikke aa opprette slack melding", response);
        }
    }

    private static class SlackConsumerException extends RuntimeException {
        public SlackConsumerException(String message, SlackResponse response) {
            super(message + " ( error: " + response.getError() + " )");
        }
    }

}
