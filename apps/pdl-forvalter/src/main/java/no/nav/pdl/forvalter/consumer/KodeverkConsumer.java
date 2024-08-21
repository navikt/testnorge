package no.nav.pdl.forvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.pdl.forvalter.config.Consumers;
import no.nav.pdl.forvalter.consumer.command.KodeverkCommand;
import no.nav.testnav.libs.dto.kodeverkservice.v1.KodeverkDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;

@Slf4j
@Service
public class KodeverkConsumer {

    private static final String POSTNUMMER = "Postnummer";
    private static final String LANDKODER = "Landkoder";
    private static final String KOMMUNER = "Kommuner2024";
    private static final String EMBETER = "Vergemål_Fylkesmannsembeter";
    private static final Random random = new SecureRandom();

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;

    public KodeverkConsumer(
            TokenExchange tokenExchange,
            Consumers consumers) {
        this.tokenExchange = tokenExchange;
        serverProperties = consumers.getKodeverkService();
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public String getTilfeldigKommune() {

        return hentKodeverk(KOMMUNER)
                .map(verdier -> verdier.keySet().stream().toList())
                .map(list -> list.get(random.nextInt(list.size())))
                .block();
    }

    public String getTilfeldigLand() {

        return hentKodeverk(LANDKODER)
                .map(landkoder -> landkoder.keySet().stream()
                        .filter(landkode -> !landkode.equals("9999") && !landkode.equals("???"))
                        .toList())
                .map(list -> list.get(random.nextInt(list.size())))
                .block();
    }

    public String getPoststedNavn(String postnummer) {

        return hentKodeverk(POSTNUMMER)
                .map(postnumre -> postnumre.get(postnummer))
                .block();
    }

    public String getEmbeteNavn(String embete) {

        return hentKodeverk(EMBETER)
                .map(embeter -> embeter.get(embete))
                .block();
    }

    private Mono<Map<String, String>> hentKodeverk(String kodeverk) {

        return tokenExchange
                .exchange(serverProperties)
                .flatMap(token -> new KodeverkCommand(webClient, kodeverk, token.getTokenValue()).call())
                .map(KodeverkDTO::getKodeverk);
    }
}
