package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.config.Consumers;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@CacheConfig
public class OrganisasjonConsumer {
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    public OrganisasjonConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient.Builder webClientBuilder) {

        serverProperties = consumers.getTestnavOrganisasjonService();
        this.tokenExchange = tokenExchange;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Cacheable("Mini-Norge-EREG")
    public Flux<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {
        return tokenExchange.exchange(serverProperties)
                .flatMapMany(accessToken -> Flux.concat(
                        orgnummerListe.stream()
                                .map(orgnummer -> new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call())
                                .collect(Collectors.toList())
                ));
    }
}
