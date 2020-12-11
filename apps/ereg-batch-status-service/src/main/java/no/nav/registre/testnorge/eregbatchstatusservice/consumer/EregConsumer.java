package no.nav.registre.testnorge.eregbatchstatusservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.eregbatchstatusservice.config.EregProperties;
import no.nav.registre.testnorge.eregbatchstatusservice.consumer.command.GetBatchStatusCommand;

@Slf4j
@Component
public class EregConsumer {
    private final Map<String, WebClient> envWebClientMap;

    public EregConsumer(EregProperties eregProperties) {
        this.envWebClientMap = eregProperties
                .getEnvHostMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> WebClient.builder().baseUrl(entry.getValue()).build()
                ));
    }


    public Long getStatusKode(String miljo, Long id) {
        if (!envWebClientMap.containsKey(miljo)) {
            throw new RuntimeException("Stotter ikke miljo: " + miljo + " i EREG.");
        } else {
            return new GetBatchStatusCommand(envWebClientMap.get(miljo), id).call();
        }
    }
}
