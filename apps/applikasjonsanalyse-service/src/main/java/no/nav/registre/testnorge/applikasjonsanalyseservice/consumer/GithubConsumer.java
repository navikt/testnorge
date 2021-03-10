package no.nav.registre.testnorge.applikasjonsanalyseservice.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.command.GetBlobCommand;
import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.command.SearchCodeCommand;
import no.nav.registre.testnorge.applikasjonsanalyseservice.consumer.dto.SearchDTO;
import no.nav.registre.testnorge.applikasjonsanalyseservice.domain.Properties;

@Component
public class GithubConsumer {
    public static final String KIND_APPLICATION_SEARCH = "\"kind: \"Application\"\"+in:file+language:yml+language:yaml+repo:";
    private final WebClient webClient;

    public GithubConsumer(@Value("${consumers.github.url}") String url) {
        this.webClient = WebClient
                .builder()
                .baseUrl(url)
                .build();
    }

    public SearchDTO search(Properties properties){
        var search = KIND_APPLICATION_SEARCH + properties.getOrganisation() + "/" + properties.getRepository();
        return new SearchCodeCommand(webClient, search).call();
    }

    public byte[] getBlob(String sha){
        return new GetBlobCommand(webClient, sha).call();
    }

}
