package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.credentials.OrganisasjonBestillingServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonBestillingCommand;
import no.nav.organisasjonforvalter.dto.responses.ItemDto;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
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
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
public class OrganisasjonBestillingConsumer {

    private final AccessTokenService accessTokenService;
    private final WebClient webClient;
    private final OrganisasjonBestillingServiceProperties serviceProperties;

    public OrganisasjonBestillingConsumer(
            OrganisasjonBestillingServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.accessTokenService = accessTokenService;
    }

    public List<ItemDto> getBestillingStatus(String uuid) {

        long startTime = currentTimeMillis();
        try {
            var accessToken = accessTokenService.generateToken(serviceProperties).block();
            var response =
                    new OrganisasjonBestillingCommand(webClient, uuid, accessToken.getTokenValue()).call();

            return Stream.of(response).collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(e.getStatusCode(), requireNonNull(e.getMessage()));

        } catch (RuntimeException e) {

            var error = format("Organisasjon-Bestilling-Service svarte ikke etter %d ms", currentTimeMillis() - startTime);
            log.error(error, e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, error);
        }
    }
}
