package no.nav.identpool.consumers;

import no.nav.identpool.config.credentials.TpsMessagingServiceProperties;
import no.nav.identpool.consumers.command.TpsMessagingGetCommand;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TpsMessagingConsumer {

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

        return new HashSet<>(getIdenterStatus(identer, null, true));
    }

    public List<TpsStatusDTO> getIdenterProdStatus(Set<String> identer) {

        return getIdenterStatus(identer, Set.of("p"), true);
    }

    private List<TpsStatusDTO> getIdenterStatus(Set<String> identer, Set<String> miljoer, boolean includeProd) {

        return tokenExchange.exchange(serviceProperties)
                .flatMapMany(token -> new TpsMessagingGetCommand(webClient, token.getTokenValue(), identer, miljoer, includeProd).call())
                .map(status -> TpsStatusDTO.builder()
                        .ident(status.getIdent())
                        .inUse(!status.getMiljoer().isEmpty())
                        .build())
                .collectList()
                .block();
    }
}
