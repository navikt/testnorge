package no.nav.registre.aareg.consumer.rs;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.config.credentials.MiljoeServiceProperties;
import no.nav.registre.aareg.consumer.rs.response.MiljoerResponse;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.List;

@Component
@Slf4j
public class MiljoerConsumer {

    private static final int TIMEOUT_S = 10;
    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final MiljoeServiceProperties serviceProperties;

    public MiljoerConsumer(
            MiljoeServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

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
        this.accessTokenService = accessTokenService;
    }

    public MiljoerResponse hentMiljoer() {

        log.info("Genererer AccessToken for {}", serviceProperties.getName());
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties).block();
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
