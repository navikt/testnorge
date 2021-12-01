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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.organisasjonforvalter.config.credentials.GenererNavnServiceProperties;
import no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Service
public class GenererNavnServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final GenererNavnServiceProperties serviceProperties;

    public GenererNavnServiceConsumer(
            GenererNavnServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public List<String> getOrgName(Integer antall) {

        long startTime = currentTimeMillis();
        try {
            var accessToken = tokenExchange.exchange(serviceProperties);
            var navn = new GenererNavnCommand(webClient, accessToken.block().getTokenValue(), antall).call();

            log.info("Generer-navn-service svarte etter {} ms", currentTimeMillis() - startTime);
            return Arrays.stream(navn)
                    .map(value -> format("%s %s", value.getAdjektiv(), value.getSubstantiv()))
                    .collect(Collectors.toList());

        } catch (WebClientResponseException e) {
            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(e.getStatusCode(), requireNonNull(e.getMessage()));

        } catch (RuntimeException e) {

            log.error(e.getMessage(), e);
            throw new HttpClientErrorException(HttpStatus.GATEWAY_TIMEOUT, e.getMessage());
        }
    }

    public String getOrgName() {

        List<String> orgName = getOrgName(1);
        return orgName.isEmpty() ? null : orgName.get(0);
    }
}
