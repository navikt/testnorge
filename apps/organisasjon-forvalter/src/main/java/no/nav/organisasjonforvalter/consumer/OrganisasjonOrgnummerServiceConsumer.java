package no.nav.organisasjonforvalter.consumer;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.util.Objects.requireNonNull;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import no.nav.organisasjonforvalter.config.credentials.OrganisasjonOrgnummerServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonOrgnummerServiceCommand;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Service
public class OrganisasjonOrgnummerServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final OrganisasjonOrgnummerServiceProperties serviceProperties;

    public OrganisasjonOrgnummerServiceConsumer(
            OrganisasjonOrgnummerServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<String> getOrgnummer(Integer antall) {

        long startTime = currentTimeMillis();
        try {
            var response = tokenExchange.exchange(serviceProperties)
                    .flatMap(token -> new OrganisasjonOrgnummerServiceCommand(webClient, antall, token.getTokenValue()).call())
                    .block();

            log.info("Orgnummer-service svarte etter {} ms", currentTimeMillis() - startTime);

            return Stream.of(response).collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(e.getStatusCode(), requireNonNull(e.getMessage()));

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
