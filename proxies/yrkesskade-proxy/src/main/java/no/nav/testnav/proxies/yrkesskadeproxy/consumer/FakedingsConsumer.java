package no.nav.testnav.proxies.yrkesskadeproxy.consumer;

import no.nav.testnav.proxies.yrkesskadeproxy.config.Consumers;
import no.nav.testnav.proxies.yrkesskadeproxy.consumer.command.FakedingsGetCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class FakedingsConsumer {

    private final WebClient webClient;

    public FakedingsConsumer(WebClient webClient, Consumers consumers) {
        this.webClient = webClient
                .mutate()
                .baseUrl(consumers.getFakedings().getUrl())
                .build();
    }

    public Mono<String> getFakeToken(String ident) {
        return new FakedingsGetCommand(webClient, ident).call();
    }

}
