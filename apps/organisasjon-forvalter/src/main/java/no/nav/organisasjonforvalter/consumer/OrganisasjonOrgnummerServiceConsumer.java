package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.credentials.OrganisasjonOrgnummerServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonOrgnummerServiceCommand;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class OrganisasjonOrgnummerServiceConsumer {

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final OrganisasjonOrgnummerServiceProperties serviceProperties;

    public OrganisasjonOrgnummerServiceConsumer(
            OrganisasjonOrgnummerServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.accessTokenService = accessTokenService;
    }

    public List<String> getOrgnummer(Integer antall) {

        long startTime = currentTimeMillis();
        try {
            var accessToken = accessTokenService.generateToken(serviceProperties);
            var response = new OrganisasjonOrgnummerServiceCommand(webClient, antall, accessToken.getTokenValue()).call();

            log.info("Orgnummer-service svarte etter {} ms", currentTimeMillis() - startTime);

            return Stream.of(response).collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());

        } catch (RuntimeException e) {
            String error = format("Organisasjon-orgnummer-service svarte ikke etter %d ms", currentTimeMillis() - startTime);
            log.error(error, e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, error);
        }
    }

    public String getOrgnummer() {

        var orgnummer = getOrgnummer(1);
        return orgnummer.isEmpty() ? null : orgnummer.get(0);
    }
}
