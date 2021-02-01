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
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.netty.http.client.HttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class OrganisasjonApiConsumer {

    private static final int TIMEOUT_S = 10;
    private static final String STATUS_URL = "/api/v1/organisasjoner/{orgnummer}";
    private static final String MILJOE = "miljo";

    private final AccessTokenService accessTokenService;
    private final AccessScopes accessScopes;
    private final WebClient webClient;

    public OrganisasjonApiConsumer(
            @Value("${organisasjon.api.url}") String baseUrl,
            @Value("${organisasjon.api.client.id}") String clientId,
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

    public OrganisasjonDto getStatus(String orgnummer, String miljoe) {

        long startTime = currentTimeMillis();

        try {
            AccessToken accessToken = accessTokenService.generateToken(accessScopes);
            ResponseEntity<OrganisasjonDto> response = webClient.get()
                    .uri(STATUS_URL.replace("{orgnummer}", orgnummer))
                    .header("Nav-Consumer-Id", "Testnorge")
                    .header("Nav-Call-Id", UUID.randomUUID().toString())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " +
                            accessToken.getTokenValue())
                    .header(MILJOE, miljoe)
                    .retrieve()
                    .toEntity(OrganisasjonDto.class)
                    .block();

            log.info("Organisasjon-API svarte med funnet etter {} ms", currentTimeMillis() - startTime);
            return nonNull(response) && response.hasBody() ? response.getBody() : new OrganisasjonDto();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new OrganisasjonDto();
            } else {
                log.error(e.getMessage(), e);
                throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
            }

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new OrganisasjonDto();
            } else {
                log.error(e.getMessage(), e);
                throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
            }

        } catch (RuntimeException e) {
            String error = format("Testnorge-organisasjon-api svarte ikke etter %d ms", currentTimeMillis() - startTime);
            log.error(error, e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, error);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrganisasjonDto {
        private String orgnummer;
        private String enhetType;
        private String navn;
        private String juridiskEnhet;
        private AdresseDto postadresse;
        private AdresseDto forretningsadresser;
        private String redigertnavn;
        private List<String> driverVirksomheter;

        public List<String> getDriverVirksomheter() {

            if (isNull(driverVirksomheter)) {
                driverVirksomheter = new ArrayList<>();
            }
            return driverVirksomheter;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdresseDto {
        private String kommunenummer;
        private String adresselinje1;
        private String adresselinje2;
        private String adresselinje3;
        private String landkode;
        private String postnummer;
        private String poststed;
    }
}
