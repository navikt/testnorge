package no.nav.testnav.dollyfasade.consumers;

import no.nav.testnav.dollyfasade.config.Consumers;
import no.nav.testnav.dollyfasade.consumers.command.GetTenorTestdataDomainCommand;
import no.nav.testnav.dollyfasade.consumers.command.PostTenorTestdataCommand;
import no.nav.testnav.dollyfasade.consumers.command.PostTenorTestdataOversiktCommand;
import no.nav.testnav.dollyfasade.dto.TenorRequestDTO;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.InfoType;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.Lookups;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorOversiktResponse;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorRequest;
import no.nav.testnav.libs.dto.tenorsearchservice.v1.TenorResponse;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static no.nav.testnav.dollyfasade.utility.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
public class TenorSearchConsumer {

    private final TokenExchange tokenService;
    private final ServerProperties serverProperties;
    private final WebClient webClient;

    public TenorSearchConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            JsonMapper jsonMapper,
            WebClient webClient) {

        serverProperties = consumers.getTenorSearchService();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(jsonMapper))
                .build();
    }

    public Mono<List<String>> getLookup(Lookups lookup) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new GetTenorTestdataDomainCommand(webClient, lookup.name(), token.getTokenValue()).call());
    }

    public Mono<TenorOversiktResponse> getTestdataOversikt(TenorRequest searchData, Integer antall, Integer side, Integer seed, Boolean ikkeFiltrer) {

        var request = TenorRequestDTO.builder()
                .query(searchData)
                .antall(antall)
                .side(side)
                .seed(seed)
                .ikkeFiltrer(ikkeFiltrer)
                .build();

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PostTenorTestdataOversiktCommand(webClient, request, token.getTokenValue()).call());
    }

    public Mono<TenorResponse> getTestdata(TenorRequest query, InfoType type, String fields, Integer antall, Integer side, Integer seed) {

        var request = TenorRequestDTO.builder()
                .query(query)
                .type(type)
                .fields(fields)
                .antall(antall)
                .side(side)
                .seed(seed)
                .build();

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PostTenorTestdataCommand(webClient, request, token.getTokenValue()).call());
    }
}
