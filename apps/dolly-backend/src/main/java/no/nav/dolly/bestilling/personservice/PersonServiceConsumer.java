package no.nav.dolly.bestilling.personservice;

import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.personservice.command.PersonServiceExistCommand;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.config.credentials.PersonServiceProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
public class PersonServiceConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;

    public PersonServiceConsumer(
            TokenExchange tokenService,
            PersonServiceProperties serverProperties,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "personService_isPerson"})
    public Mono<PersonServiceResponse> isPerson(String ident, Set<String> opplysningId) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new PersonServiceExistCommand(webClient, ident, opplysningId, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-person-service";
    }

}
