package no.nav.testnav.identpool.consumers;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.identpool.config.Consumers;
import no.nav.testnav.identpool.consumers.command.TpsMessagingGetCommand;
import no.nav.testnav.identpool.consumers.command.TpsValidation;
import no.nav.testnav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class TpsMessagingConsumer {

    private static final String NO_ENV = "pp";
    private static final int PAGESIZE = 80;

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;

    public TpsMessagingConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTpsMessagingService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
        this.tokenExchange = tokenExchange;
    }

    public Flux<TpsStatusDTO> getIdenterStatuser(Set<String> identer) {
        return getIdenterStatus(new ArrayList<>(identer), Set.of("zz"), status -> !status.getMiljoer().isEmpty());
    }

    public Flux<TpsStatusDTO> getIdenterProdStatus(Set<String> identer) {
        return getIdenterStatus(new ArrayList<>(identer), Set.of(NO_ENV), status -> status.getMiljoer().contains("p"));
    }

    private Flux<TpsStatusDTO> getIdenterStatus(List<String> identer, Set<String> miljoer, TpsValidation validation) {

//        var startTid = System.currentTimeMillis();

        return tokenExchange.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / PAGESIZE + 1)
                        .flatMap(page -> new TpsMessagingGetCommand(webClient, token.getTokenValue(),
                                identer.subList(page * PAGESIZE, Math.min(identer.size(), (page + 1) * PAGESIZE)),
                                miljoer, true).call()
                                .map(status -> TpsStatusDTO.builder()
                                        .ident(status.getIdent())
                                        .inUse(validation.apply(status))
                                        .build())));

//        log.info("Kall til TPS med {} identer tok {} ms", identer.size(), System.currentTimeMillis() - startTid);

//        return response;
    }
}
