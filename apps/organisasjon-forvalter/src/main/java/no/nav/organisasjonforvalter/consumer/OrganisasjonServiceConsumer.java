package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.credentials.OrganisasjonServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonServiceCommand;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static java.lang.System.currentTimeMillis;
import static java.util.Objects.nonNull;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final OrganisasjonServiceProperties serviceProperties;

    public OrganisasjonServiceConsumer(
            OrganisasjonServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.accessTokenService = accessTokenService;
    }

    public OrganisasjonDTO getStatus(String orgnummer, String miljoe) {

        long startTime = currentTimeMillis();

        try {
            var accessToken = accessTokenService.generateToken(serviceProperties);
            var response =
                    new OrganisasjonServiceCommand(webClient, orgnummer, miljoe, accessToken.getTokenValue()).call();

            log.info("Organisasjon-Service svarte med funnet etter {} ms", currentTimeMillis() - startTime);
            return nonNull(response) ? response : new OrganisasjonDTO();

        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new OrganisasjonDTO();
            } else {
                log.error(e.getMessage(), e);
                throw new HttpClientErrorException(e.getStatusCode(), e.getResponseBodyAsString());
            }

        } catch (RuntimeException e) {

            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, e.getMessage());
        }
    }
}
