package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.AdresseServiceProperties;
import no.nav.pdl.forvalter.consumer.command.MatrikkeladresseServiceCommand;
import no.nav.pdl.forvalter.consumer.command.VegadresseServiceCommand;
import no.nav.testnav.libs.dto.pdlforvalter.v1.MatrikkeladresseDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.VegadresseDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public AdresseServiceConsumer(AccessTokenService accessTokenService, AdresseServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public no.nav.testnav.libs.dto.adresseservice.v1.VegadresseDTO getVegadresse(VegadresseDTO vegadresse, String matrikkelId) {

        var startTime = currentTimeMillis();

        try {
            var adresser = accessTokenService.generateToken(properties).flatMap(
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
            var adresser = accessTokenService.generateToken(properties).flatMap(
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
