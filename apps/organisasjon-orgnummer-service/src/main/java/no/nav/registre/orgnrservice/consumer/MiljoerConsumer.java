package no.nav.registre.orgnrservice.consumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;

import no.nav.registre.orgnrservice.config.credentials.MiljoerServiceProperties;
import no.nav.registre.orgnrservice.consumer.response.MiljoerResponse;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class MiljoerConsumer {

    private static final int TIMEOUT_S = 10;
    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final MiljoerServiceProperties serviceProperties;

    public MiljoerConsumer(
            MiljoerServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create()
                                .tcpConfiguration(tcpClient -> tcpClient
                                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT_S * 1000)
                                        .doOnConnected(connection ->
                                                connection
                                                        .addHandlerLast(new ReadTimeoutHandler(TIMEOUT_S))
                                                        .addHandlerLast(new WriteTimeoutHandler(TIMEOUT_S))))))
                .build();
        this.tokenExchange = tokenExchange;
    }

    public MiljoerResponse hentMiljoer() {

        log.info("Genererer AccessToken for {}", serviceProperties.getName());
        var accessToken = tokenExchange.generateToken(serviceProperties).block();
        List<String> response = webClient
                .get()
                .uri(MILJOER_URL)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .block();

        if (response == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Fant ingen miljøer");
        }
        return new MiljoerResponse(response);
    }
}
