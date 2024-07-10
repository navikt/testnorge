package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.command.HentPersonerCommand;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.domain.TenorRawResponse;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class TenorConsumer {
    private static final String TENOR_DOMAIN = "https://testnav-tenor-search-service.intern.dev.nav.no";
    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties serverProperties;
    private final ObjectMapper objectMapper;


    public TenorConsumer(
            TokenExchange tokenExchange,
            Consumers consumers,
            ObjectMapper objectMapper) {

        this.serverProperties = consumers.getTenorSearchService();
        this.tokenExchange = tokenExchange;
        this.objectMapper = objectMapper;

        ExchangeStrategies jacksonStrategy = ExchangeStrategies
                .builder()
                .codecs(
                        config -> {
                            config
                                    .defaultCodecs()
                                    .jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
                            config
                                    .defaultCodecs()
                                    .jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
                        })
                .build();

        this.webClient = WebClient
                .builder()
                .exchangeStrategies(jacksonStrategy)
                .baseUrl(TENOR_DOMAIN)
                .build();
    }

    public void consume() throws JsonProcessingException {
        System.out.println("Kjører consume");
        var accessToken = tokenExchange.exchange(serverProperties).block();
        System.out.println("Har hentet ut token");

        if (nonNull(accessToken)) {
            var token = accessToken.getTokenValue();
            HentPersonerCommand commander = new HentPersonerCommand(token, webClient);
            JsonNode data = commander.hentPersonData();
            var rawResponse = objectMapper.readValue(data.toString(), TenorRawResponse.class);
            System.out.println(rawResponse.getDokumentListe().getFirst().getBostedsadresse().toString());
        }
    }
}