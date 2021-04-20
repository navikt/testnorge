package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.credentials.AdresseServiceProperties;
import no.nav.organisasjonforvalter.consumer.command.AdresseServiceCommand;
import no.nav.organisasjonforvalter.dto.responses.AdresseResponse;
import no.nav.organisasjonforvalter.dto.responses.PdlAdresseResponse;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static java.lang.System.currentTimeMillis;

@Slf4j
@Service
public class AdresseServiceConsumer {

    private final WebClient webClient;
    private final AdresseServiceProperties serviceProperties;
    private final AccessTokenService accessTokenService;

    public AdresseServiceConsumer(
            AdresseServiceProperties serviceProperties,
            AccessTokenService accessTokenService) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.accessTokenService = accessTokenService;
    }

    private static AdresseResponse getDefaultADresse() {

        return AdresseResponse.builder()
                .vegadresser(List.of(
                        PdlAdresseResponse.Vegadresse.builder()
                                .matrikkelId("285693617")
                                .adressenavn("FYRSTIKKALLÉEN")
                                .postnummer("0661")
                                .husnummer(1)
                                .kommunenummer("0301")
                                .build()))
                .build();
    }

    public AdresseResponse getAdresser(String postnr, String kommunenr) {

        long startTime = currentTimeMillis();

        try {
            AccessToken accessToken = accessTokenService.generateToken(serviceProperties);
            AdresseResponse adresseResponse =
                    new AdresseServiceCommand(webClient, postnr, kommunenr, accessToken.getTokenValue()).call();

            log.info("Adresseoppslag tok {} ms", currentTimeMillis() - startTime);
            return adresseResponse;

        } catch (RuntimeException e) {

            log.error("Henting av adresse feilet", e);
            return getDefaultADresse();
        }
    }
}
