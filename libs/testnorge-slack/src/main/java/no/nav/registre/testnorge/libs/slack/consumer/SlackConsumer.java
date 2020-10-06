package no.nav.registre.testnorge.libs.slack.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.ProxyProvider;

import java.net.URI;

import no.nav.registre.testnorge.libs.slack.command.PublishMessageCommand;
import no.nav.registre.testnorge.libs.slack.dto.Message;
import no.nav.registre.testnorge.libs.slack.dto.SlackResponse;

@Slf4j
public class SlackConsumer {
    private final String token;
    private final WebClient webClient;

    public SlackConsumer(
            String token,
            String baseUrl,
            String proxyHost
    ) {
        this.token = token;
        var builder = WebClient.builder();
        if (proxyHost != null) {
            log.info("Setter opp proxy host {} for Slack api", proxyHost);
            var uri = URI.create(proxyHost);

            HttpClient httpClient = HttpClient
                    .create()
                    .tcpConfiguration(tcpClient -> tcpClient.proxy(proxy -> proxy
                            .type(ProxyProvider.Proxy.HTTP)
                            .host(uri.getHost())
                            .port(uri.getPort())
                    ));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }
        webClient = builder
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
