package no.nav.dolly.bestilling.aareg.amelding;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.command.OrganisasjonGetCommand;
import no.nav.dolly.config.credentials.OrganisasjonServiceProperties;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class OrganisasjonServiceConsumer {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public OrganisasjonServiceConsumer(TokenExchange tokenService,
                                       OrganisasjonServiceProperties serviceProperties,
                                       ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    public Flux<OrganisasjonDTO> getOrganisasjoner(Set<String> orgnummerListe, String miljo) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(orgnummerListe)
                        .flatMap(orgnummer ->
                                new OrganisasjonGetCommand(webClient, orgnummer, miljo, token.getTokenValue()).call()));
    }

    public Map<String, String> checkAlive() {

        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }
}