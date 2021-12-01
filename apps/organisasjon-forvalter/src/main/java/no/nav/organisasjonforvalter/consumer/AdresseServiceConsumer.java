package no.nav.organisasjonforvalter.consumer;

import static java.lang.System.currentTimeMillis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;

import no.nav.organisasjonforvalter.config.credentials.AdresseServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.AdresseServiceCommand;
import no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private final WebClient webClient;
    private final AdresseServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public AdresseServiceConsumer(
            AdresseServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    private static VegadresseDTO getDefaultAdresse() {

        return VegadresseDTO.builder()
                .matrikkelId("285693617")
                .adressenavn("FYRSTIKKALLÉEN")
                .postnummer("0661")
                .husnummer(2)
                .kommunenummer("0301")
                .build();
    }

    public List<VegadresseDTO> getAdresser(String query) {

        long startTime = currentTimeMillis();

        try {
            var adresser = tokenExchange.exchange(serviceProperties)
                    .flatMap(token -> new AdresseServiceCommand(webClient, query, token.getTokenValue()).call()).block();

            log.info("Adresseoppslag tok {} ms", currentTimeMillis() - startTime);
            return Arrays.asList(adresser);

        } catch (RuntimeException e) {

            log.error("Henting av adresse feilet", e);
            return List.of(getDefaultAdresse());
        }
    }
}
