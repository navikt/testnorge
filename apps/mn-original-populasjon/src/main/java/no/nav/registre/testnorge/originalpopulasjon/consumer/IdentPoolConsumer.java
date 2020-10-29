package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.originalpopulasjon.consumer.command.GetIdenterCommand;
import no.nav.registre.testnorge.originalpopulasjon.domain.Aldersspenn;

@Slf4j
@Component
@DependencyOn("ident-pool")
public class IdentPoolConsumer {

    private final WebClient webClient;
    private final String applicationName;

    public IdentPoolConsumer(@Value("${consumer.identpool.url}") String url, ApplicationProperties applicationProperties) {
        this.applicationName = applicationProperties.getName();
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public List<String> getIdenter(Integer antall, Aldersspenn aldersspenn) {
        return new GetIdenterCommand(webClient, applicationName, antall, aldersspenn).call();
    }
}
