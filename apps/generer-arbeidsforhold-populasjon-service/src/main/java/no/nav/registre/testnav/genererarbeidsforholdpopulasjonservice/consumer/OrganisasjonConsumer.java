package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.credentials.OrganisasjonServiceProperties;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
@CacheConfig
public class OrganisasjonConsumer {
    private final OrganisasjonServiceProperties serviceProperties;
    private final TokenExchange tokenExchange;
    private final WebClient webClient;

    public OrganisasjonConsumer(
            OrganisasjonServiceProperties serviceProperties,
            TokenExchange tokenExchange
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Cacheable("Mini-Norge-EREG")
    public Flux<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {
        return tokenExchange.exchange(serviceProperties)
                .flatMapMany(accessToken -> Flux.concat(
                        orgnummerListe.stream()
                                .map(orgnummer -> new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call())
                                .collect(Collectors.toList())
                ));
    }
}
