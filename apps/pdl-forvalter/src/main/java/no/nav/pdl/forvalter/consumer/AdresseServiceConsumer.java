package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.AdresseServiceProperties;
import no.nav.pdl.forvalter.consumer.command.AdresseServiceCommand;
import no.nav.pdl.forvalter.domain.PdlVegadresse;
import no.nav.registre.testnorge.libs.dto.adresseservice.v1.VegadresseDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.stream.Stream;

import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

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

    private static String filterArtifact(String artifact) {
        return isNotBlank(artifact) ? artifact : "";
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

    public VegadresseDTO getAdresse(PdlVegadresse vegadresse, String matrikkelId) {

        var startTime = currentTimeMillis();
        var query = new StringBuilder()
                .append("matrikkelId=")
                .append(filterArtifact(matrikkelId))
                .append("&adressenavn=")
                .append(filterArtifact(vegadresse.getAdressenavn()))
                .append("&husnummer=")
                .append(filterArtifact(vegadresse.getHusnummer()))
                .append("&husbokstav=")
                .append(filterArtifact(vegadresse.getHusbokstav()))
                .append("&postnummer=")
                .append(filterArtifact(vegadresse.getPostnummer()))
                .append("&kommunenummer=")
                .append(filterArtifact(vegadresse.getKommunenummer()))
                .append("&tilleggsnavn=")
                .append(filterArtifact(vegadresse.getAdressetilleggsnavn()))
                .toString();

        try {
            var adresser = accessTokenService.generateToken(properties).flatMap(
                    token -> new AdresseServiceCommand(webClient, query, token.getTokenValue()).call())
                    .block();

            log.info("Oppslag til adresseservice tok {} ms", currentTimeMillis() - startTime);
            return Stream.of(adresser).findFirst().orElse(getDefaultAdresse());

        } catch (RuntimeException e) {

            log.info("Oppslag til adresseservice feilet etter {} ms", currentTimeMillis() - startTime);
            return getDefaultAdresse();
        }
    }
}
