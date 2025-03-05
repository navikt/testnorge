package no.nav.organisasjonforvalter.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.organisasjonforvalter.config.Consumers;
import no.nav.organisasjonforvalter.consumer.command.OrganisasjonServiceCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final TokenExchange tokenExchange;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public OrganisasjonServiceConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient) {
        serverProperties = consumers.getTestnavOrganisasjonService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<Map<String, OrganisasjonDTO>> getStatus(String orgnummer, String miljoe) {

        return getStatus(Set.of(orgnummer), Set.of(miljoe));
    }

    public Flux<Map<String, OrganisasjonDTO>> getStatus(Set<String> orgnummere, Set<String> miljoer) {

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> Flux.fromIterable(miljoer)
                        .map(miljoe -> Flux.fromIterable(orgnummere)
                                .flatMap(orgnr -> new OrganisasjonServiceCommand(webClient, orgnr, miljoe, token.getTokenValue()).call())
                                .collect(Collectors.toMap(orgMap -> miljoe, orgMap -> orgMap))))
                .flatMap(Mono::flux);
    }
}
