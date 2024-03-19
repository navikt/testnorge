package no.nav.registre.testnorge.eregbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.eregbatchstatusservice.config.Consumers;
import no.nav.registre.testnorge.eregbatchstatusservice.config.EregProperties;
import no.nav.registre.testnorge.eregbatchstatusservice.consumer.command.GetBatchStatusCommand;
import no.nav.testnav.libs.reactivesecurity.exchange.azuread.TrygdeetatenAzureAdTokenService;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EregConsumer {
    private final Map<String, WebClient> envWebClientMap;
    private final ServerProperties serverProperties;
    TrygdeetatenAzureAdTokenService tokenService;

    public EregConsumer(EregProperties eregProperties,
                        TrygdeetatenAzureAdTokenService tokenService,
                        Consumers consumers
    ) {

        this.tokenService = tokenService;
        this.serverProperties = consumers.getEregAura();
        this.envWebClientMap = eregProperties
                .getEnvHostMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            log.info("Registrerer WebClient for miljo: " + entry.getKey() + " med url: " + entry.getValue());
                            return WebClient.builder()
                                    .baseUrl(entry.getValue())
                                    .build();
                        }
                ));
    }

    public Long getStatusKode(String miljo, Long id) {
        var token = tokenService.exchange(serverProperties).block();
        if (!envWebClientMap.containsKey(miljo)) {
            throw new RuntimeException("Stotter ikke miljo: " + miljo + " i EREG.");
        } else {
            String tokenValue = Optional.ofNullable(token).map(AccessToken::getTokenValue).orElse(null);
            return new GetBatchStatusCommand(envWebClientMap.get(miljo), id, tokenValue).call();
        }
    }
}