package no.nav.registre.testnorge.synt.person.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import no.nav.registre.testnorge.synt.person.consumer.command.GetIdentCommand;

@Component
public class IdentPoolConsumer {
    private final WebClient webClient;
    private final String applicationName;

    public IdentPoolConsumer(@Value("${consumers.identpool.url}") String url, ApplicationProperties applicationProperties) {
        this.applicationName = applicationProperties.getName();
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public String getIdent() {
        return new GetIdentCommand(webClient, applicationName).call();
    }
}
