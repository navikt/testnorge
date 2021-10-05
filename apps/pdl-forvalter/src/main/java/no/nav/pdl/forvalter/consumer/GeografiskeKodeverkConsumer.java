package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.GeografiskeKodeverkServiceProperties;
import no.nav.pdl.forvalter.consumer.command.GeografiskeKodeverkCommand;
import no.nav.testnav.libs.dto.geografiskekodeverkservice.v1.GeografiskeKodeverkDTO;
import no.nav.testnav.libs.servletsecurity.config.ServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.security.SecureRandom;
import java.util.List;

@Slf4j
@Service
public class GeografiskeKodeverkConsumer {

    private static final String POSTNUMMER_URL = "/api/v1/postnummer";
    private static final String LAND_URL = "/api/v1/land";
    private static final String KOMMUNE_URL = "/api/v1/kommuner";
    private static final String EMBETE_URL = "/api/vi/embeter";

    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final ServerProperties properties;

    public GeografiskeKodeverkConsumer(AccessTokenService accessTokenService,
                                       GeografiskeKodeverkServiceProperties properties) {

        this.accessTokenService = accessTokenService;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    // TODO Trenger vi denne??
    public List<GeografiskeKodeverkDTO> getKommuner(String kommunenr, String kommunenavn) {

        return accessTokenService.generateToken(properties).flatMap(
                        token -> new GeografiskeKodeverkCommand(webClient, KOMMUNE_URL,
                                String.format("kommunenr=%s&kommunenavn=%s", kommunenr, kommunenavn), token.getTokenValue()).call())
                .block();
    }

    public String getTilfeldigKommune() {

        List<GeografiskeKodeverkDTO> kommuner = accessTokenService.generateToken(properties).flatMap(
                token -> new GeografiskeKodeverkCommand(webClient, KOMMUNE_URL, null, token.getTokenValue()).call()).block();

        return kommuner.get(new SecureRandom().nextInt(kommuner.size())).kode();
    }

    public String getTilfeldigLand() {

        List<GeografiskeKodeverkDTO> land = accessTokenService.generateToken(properties).flatMap(
                token -> new GeografiskeKodeverkCommand(webClient, LAND_URL, null, token.getTokenValue()).call()).block();

        return land.get(new SecureRandom().nextInt(land.size())).kode();
    }

    public String getPoststedNavn(String postnummer) {

        List<GeografiskeKodeverkDTO> poststed = accessTokenService.generateToken(properties).flatMap(
                token -> new GeografiskeKodeverkCommand(webClient, POSTNUMMER_URL, postnummer, token.getTokenValue()).call()).block();

        return poststed.get(0).navn();
    }

    //TODO Trenger vi denne?
    public List<GeografiskeKodeverkDTO> getEmbeter() {

        return accessTokenService.generateToken(properties).flatMap(
                token -> new GeografiskeKodeverkCommand(webClient, EMBETE_URL, null, token.getTokenValue()).call()).block();
    }

    public String getEmbeteNavn(String embete) {

        List<GeografiskeKodeverkDTO> embeter = accessTokenService.generateToken(properties).flatMap(
                token -> new GeografiskeKodeverkCommand(webClient, EMBETE_URL, embete, token.getTokenValue()).call()).block();

        return embeter.get(0).navn();
    }
}
