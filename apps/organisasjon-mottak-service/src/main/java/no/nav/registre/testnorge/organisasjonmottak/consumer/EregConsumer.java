package no.nav.registre.testnorge.organisasjonmottak.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.organisasjonmottak.config.EregProperties;
import no.nav.registre.testnorge.organisasjonmottak.consumer.command.UpdateEregIndexCommand;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;

@Slf4j
@Component
public class EregConsumer {

    private final JenkinsConsumer jenkinsConsumer;
    private final Map<String, WebClient> envWebClientMap;

    public EregConsumer(JenkinsConsumer jenkinsConsumer, EregProperties eregProperties) {
        this.jenkinsConsumer = jenkinsConsumer;
        this.envWebClientMap = eregProperties
                .getEnvHostMap()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> WebClient.builder().baseUrl(entry.getValue()).build()
                ));
    }


    public void save(Flatfil flatfil, String miljo, Set<String> uuids) {
        jenkinsConsumer.send(flatfil, miljo, uuids);
    }

    public void updateIndex(String miljo) {
        if (!envWebClientMap.containsKey(miljo)) {
            throw new RuntimeException("Stotter ikke indeksering av miljo: " + miljo);
        } else {
            log.info("Starter indeksering av EREG {}.", miljo);
            new UpdateEregIndexCommand(envWebClientMap.get(miljo), LocalDateTime.now()).run();
        }
    }

}
