package no.nav.testnav.proxies.arbeidsplassencvproxy.consumer;

import tools.jackson.databind.ObjectMapper;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.command.FakedingsGetCommand;
import no.nav.testnav.proxies.arbeidsplassencvproxy.util.JacksonExchangeStrategyUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FakedingsConsumer {

    private static final String FAKE_TOKENDINGS_URL = "https://fakedings.intern.dev.nav.no";

    private final WebClient webClient;

    public FakedingsConsumer(ObjectMapper objectMapper, WebClient webClient) {
        this.webClient = webClient
                .mutate()
                .baseUrl(FAKE_TOKENDINGS_URL)
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy())
                .build();
    }

    public Mono<String> getFakeToken(String ident) {
        return new FakedingsGetCommand(webClient, ident).call();
    }

}
