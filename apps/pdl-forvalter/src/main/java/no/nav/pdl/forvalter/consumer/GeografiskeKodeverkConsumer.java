package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.credentials.GeografiskeKodeverkServiceProperties;
import no.nav.pdl.forvalter.consumer.command.GeografiskeKodeverkCommand;
import no.nav.testnav.libs.dto.geografiskekodeverkservice.v1.GeografiskeKodeverkDTO;
import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.security.SecureRandom;
import java.time.Duration;

@Slf4j
@Service
public class GeografiskeKodeverkConsumer {

    private static final String POSTNUMMER_URL = "/api/v1/postnummer";
    private static final String LAND_URL = "/api/v1/land";
    private static final String KOMMUNE_URL = "/api/v1/kommuner";
    private static final String EMBETE_URL = "/api/v1/embeter";

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties properties;
    private Flux<GeografiskeKodeverkDTO> kommuneKodeverk;
    private Flux<GeografiskeKodeverkDTO> landkodeverk;

    public GeografiskeKodeverkConsumer(TokenExchange tokenExchange,
                                       GeografiskeKodeverkServiceProperties properties) {

        this.tokenExchange = tokenExchange;
        this.properties = properties;
        this.webClient = WebClient
                .builder()
                .baseUrl(properties.getUrl())
                .build();
    }

    private Flux<GeografiskeKodeverkDTO> cache(String url) {
        return tokenExchange
                .generateToken(properties)
                .flatMapMany(token -> new GeografiskeKodeverkCommand(webClient, url, null, token.getTokenValue()).call())
                .cache(Duration.ofDays(7));
    }

    public String getTilfeldigKommune() {
        if (kommuneKodeverk == null) {
            this.kommuneKodeverk = cache(KOMMUNE_URL);
        }
        return kommuneKodeverk
                .collectList()
                .blockOptional()
                .map(list -> list.get(new SecureRandom().nextInt(list.size())))
                .map(GeografiskeKodeverkDTO::kode)
                .orElse(null);
    }

    public String getTilfeldigLand() {
        if (landkodeverk == null) {
            this.landkodeverk = cache(LAND_URL);
        }
        return landkodeverk
                .collectList()
                .blockOptional()
                .map(list -> list.get(new SecureRandom().nextInt(list.size())))
                .map(GeografiskeKodeverkDTO::kode)
                .orElse(null);
    }

    public String getPoststedNavn(String postnummer) {
        return tokenExchange
                .generateToken(properties)
                .flatMapMany(token -> new GeografiskeKodeverkCommand(webClient, POSTNUMMER_URL, postnummer, token.getTokenValue()).call())
                .next()
                .blockOptional()
                .map(GeografiskeKodeverkDTO::navn)
                .orElse("UKJENT");
    }

    public String getEmbeteNavn(String embete) {
        return tokenExchange
                .generateToken(properties)
                .flatMapMany(token -> new GeografiskeKodeverkCommand(webClient, EMBETE_URL, embete, token.getTokenValue()).call())
                .next()
                .blockOptional()
                .map(GeografiskeKodeverkDTO::navn)
                .orElse(embete);
    }
}
