package no.nav.testnav.proxies.arbeidsplassencvproxy.consumer;

import tools.jackson.databind.ObjectMapper;
import no.nav.testnav.proxies.arbeidsplassencvproxy.config.Consumers;
import no.nav.testnav.proxies.arbeidsplassencvproxy.consumer.command.FakedingsGetCommand;
import no.nav.testnav.proxies.arbeidsplassencvproxy.util.JacksonExchangeStrategyUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FakedingsConsumer {

    private final WebClient webClient;

    public FakedingsConsumer(ObjectMapper objectMapper, WebClient webClient, Consumers consumers) {
        this.webClient = webClient
                .mutate()
                .baseUrl(consumers.getFakedings().getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy())
                .build();
    }

    public Mono<String> getFakeToken(String ident) {
        return new FakedingsGetCommand(webClient, ident).call();
    }

}
