package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.AdresseServiceProperties;
import no.nav.pdl.forvalter.consumer.command.AdresseServiceGetCommand;
import no.nav.pdl.forvalter.consumer.command.AdresseServicePostCommand;
import no.nav.pdl.forvalter.dto.AdresseRequest;
import no.nav.pdl.forvalter.dto.AdresseResponse;
import no.nav.pdl.forvalter.dto.PdlAdresseResponse;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class AddressServiceConsumer {

    private static final String ADRESSER_KOMMUNE_URL = "/api/v1/adresser/kommunenummer/{kommunenummer}";
    private static final String ADRESSER_POSTNR_URL = "/api/v1/adresser/postnummer/{postnummer}";
    private static final String ADRESSER_AUTO_URL = "/api/v1/adresser/auto";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties properties;

    public AddressServiceConsumer(AccessTokenService accessTokenService, AdresseServiceProperties properties) {
        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    public PdlAdresseResponse.Vegadresse getAdresserAuto(AdresseRequest request) {
        return getAdresserAuto(request, 1).getVegadresser().stream().findFirst().orElse(null);
    }

    public AdresseResponse getAdresserAuto(AdresseRequest request, Integer antall) {
        var accessToken = accessTokenService.generateToken(properties);
        return new AdresseServicePostCommand(webClient, ADRESSER_AUTO_URL, antall, request, accessToken.getTokenValue()).call();
    }

    public PdlAdresseResponse.Vegadresse getAdresseFromPostnummer(String postnummer) {
        return getAdresseFromPostnummer(postnummer, 1).getVegadresser().stream().findFirst().orElse(null);
    }

    public AdresseResponse getAdresseFromPostnummer(String postnummer, Integer antall) {
        var accessToken = accessTokenService.generateToken(properties);
        return new AdresseServiceGetCommand(webClient,
                ADRESSER_POSTNR_URL.replace("{postnummer}", postnummer), antall, accessToken.getTokenValue()).call();
    }

    public PdlAdresseResponse.Vegadresse getAdresseFromKommunenummer(String kommunenummer) {
        return getAdresseFromKommunenummer(kommunenummer, 1).getVegadresser().stream().findFirst().orElse(null);
    }

    public AdresseResponse getAdresseFromKommunenummer(String kommunenummer, Integer antall) {
        var accessToken = accessTokenService.generateToken(properties);
        return new AdresseServiceGetCommand(webClient,
                ADRESSER_KOMMUNE_URL.replace("{kommunenummer}", kommunenummer), antall, accessToken.getTokenValue()).call();
    }
}
