package no.nav.identpool.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.config.credentials.TpsMessagingServiceProperties;
import no.nav.identpool.consumers.command.TpsMessagingGetCommand;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TpsMessagingConsumer {

    private static final int PAGESIZE = 80;

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenExchange;

    public TpsMessagingConsumer(
            TpsMessagingServiceProperties serviceProperties,
            TokenExchange tokenExchange) {

        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Set<TpsStatusDTO> getIdenterStatuser(Set<String> identer) {

        return new HashSet<>(getIdenterStatus(new ArrayList<>(identer), null, true));
    }

    public List<TpsStatusDTO> getIdenterProdStatus(Set<String> identer) {

        return getIdenterStatus(new ArrayList<>(identer), Set.of("p"), true);
    }

    private List<TpsStatusDTO> getIdenterStatus(List<String> identer, Set<String> miljoer, boolean includeProd) {

        var startTid = System.currentTimeMillis();

        var response = tokenExchange.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / PAGESIZE + 1)
                        .flatMap(page -> new TpsMessagingGetCommand(webClient, token.getTokenValue(),
                                identer.subList(page * PAGESIZE, Math.min(identer.size(), (page + 1) * PAGESIZE)),
                                miljoer, includeProd).call()
                                .map(status -> TpsStatusDTO.builder()
                                        .ident(status.getIdent())
                                        .inUse(!status.getMiljoer().isEmpty())
                                        .build())))
                .collectList()
                .block();

        log.info("Kall til TPS med {} identer tok {} sekunder", identer.size(), (System.currentTimeMillis() - startTid) / 1000);

        return response;
    }
}
