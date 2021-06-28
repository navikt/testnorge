package no.nav.registre.aareg.consumer.rs;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.config.credentials.ArbeidsforholdServiceProperties;
import no.nav.registre.aareg.domain.Arbeidsforhold;
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

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ArbeidsforholdServiceConsumer {

    private static final int TIMEOUT_S = 10;
    private static final String ARBEIDSTAKER_URL = "/api/v2/arbeidstaker";

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final ArbeidsforholdServiceProperties serviceProperties;

    public ArbeidsforholdServiceConsumer(
            ArbeidsforholdServiceProperties serviceProperties,
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

    public List<Arbeidsforhold> hentArbeidsforhold(String ident, String miljoe) {

        log.info("Genererer AccessToken for {}", serviceProperties.getName());
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties).block();
        List<Arbeidsforhold> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(String.format("%s/%s/arbeidsforhold", ARBEIDSTAKER_URL, ident)).build())
                .header("miljo", miljoe)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken.getTokenValue())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Arbeidsforhold>>() {
                })
                .block();

        if (response == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Fant ingen arbeidsforhold");
        }
        return new ArrayList<>();
    }
}
