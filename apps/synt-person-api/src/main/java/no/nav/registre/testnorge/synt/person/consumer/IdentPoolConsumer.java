package no.nav.registre.testnorge.synt.person.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.synt.person.consumer.command.GetIdentCommand;

@Component
@DependencyOn("ident-pool")
public class IdentPoolConsumer {
    private final WebClient webClient;
    private final String applicationName;

    public IdentPoolConsumer(@Value("${consumers.identpool.url}") String url, @Value("${application.name}") String applicationName) {
        this.applicationName = applicationName;
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public String getIdent() {
        return new GetIdentCommand(webClient, applicationName).call();
    }
}
