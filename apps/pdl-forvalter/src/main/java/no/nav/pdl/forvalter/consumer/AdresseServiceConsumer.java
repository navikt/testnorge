package no.nav.pdl.forvalter.consumer;

import static java.lang.System.currentTimeMillis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.stream.Stream;

import no.nav.pdl.forvalter.config.credentials.AdresseServiceProperties;
import no.nav.pdl.forvalter.consumer.command.MatrikkeladresseServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;

    public AdresseServiceConsumer(TokenExchange tokenExchange, AdresseServiceProperties properties) {
        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO getVegadresse(VegadresseDTO vegadresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        try {
            var adresser = tokenExchange.exchange(properties).flatMap(
                            token -> new VegadresseServiceCommand(webClient, vegadresse, matrikkelId, token.getTokenValue()).call())
                    .block();

            log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime);
            return Stream.of(adresser).findFirst()
                    .orElse(VegadresseServiceCommand.defaultAdresse());

        } catch (WebClientResponseException e) {

            log.info("Oppslag til adresseservice feilet etter {} ms, {}, request: {}", currentTimeMillis() - startTime,
                    e.getResponseBodyAsString(), vegadresse.toString());
            return VegadresseServiceCommand.defaultAdresse();
        }
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.MatrikkeladresseDTO getMatrikkeladresse(MatrikkeladresseDTO adresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        try {
            var adresser = tokenExchange.exchange(properties).flatMap(
                            token -> new MatrikkeladresseServiceCommand(webClient, adresse, matrikkelId, token.getTokenValue()).call())
                    .block();

            log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime);
            return Stream.of(adresser).findFirst()
                    .orElse(MatrikkeladresseServiceCommand.defaultAdresse());

        } catch (WebClientResponseException e) {

            log.info("Oppslag til adresseservice feilet etter {} ms, {}, request: {}", currentTimeMillis() - startTime,
                    e.getResponseBodyAsString(), adresse.toString());
            return MatrikkeladresseServiceCommand.defaultAdresse();
        }
    }
}
