package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetLevendeIdenterCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.HodejegerenServerProperties;
import no.nav.testnav.libs.servletsecurity.service.AccessTokenService;

@Slf4j
@Component
public class HodejegerenConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final HodejegerenServerProperties serverProperties;

    public HodejegerenConsumer(
            AccessTokenService accessTokenService,
            HodejegerenServerProperties serverProperties
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder().baseUrl(serverProperties.getUrl()).build();
    }

    public Flux<String> getIdenter(String miljo, int antall) {
        var accessToken = accessTokenService.generateToken(serverProperties).block();
        return new GetLevendeIdenterCommand(webClient, miljo, antall, accessToken.getTokenValue()).call();
    }
}
