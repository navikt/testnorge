package no.nav.organisasjonforvalter.consumer;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class OrganisasjonNavnConsumer {

    private static final int TIMEOUT_S = 10;
    private static final String NAME_URL = "/api/v1/navn?antall=";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonNavnConsumer(
            @Value("${organisasjon.navn.url}") String baseUrl,
            @Value("${organisasjon.navn.client.id}") String clientId,
            AccessTokenService accessTokenService) {

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
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
        this.accessScopes = new AccessScopes("api://" + clientId + "/.default");
    }

    public List<String> getOrgName(Integer antall) {

        long startTime = currentTimeMillis();
        try {
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<Navn[]> response = webClient.get()
                    .uri(NAME_URL + antall.toString())
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .retrieve()
                    .toEntity(Navn[].class)
                    .block();

            List<Navn> orgNavn = response.hasBody() ? List.of(response.getBody()) : Collections.emptyList();
            log.info("Generer-navn-service svarte etter {} ms", currentTimeMillis() - startTime);

            return orgNavn.stream().map(Navn::toString).collect(Collectors.toList());

        } catch (HttpClientErrorException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());

        } catch (RuntimeException e) {

            String error = format("Generer-navn-service svarte ikke etter %d ms", currentTimeMillis() - startTime);
            log.error(error, e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, error);
        }
    }

    public String getOrgName() {

        List<String> orgName = getOrgName(1);
        return orgName.isEmpty() ? null : orgName.get(0);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Navn {

        private String adjektiv;
        private String substantiv;

        @Override
        public String toString() {
            return format("%s %s", adjektiv, substantiv);
        }
    }
}
