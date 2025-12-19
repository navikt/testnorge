package no.nav.testnav.pdllagreservice.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdllagreservice.consumers.OpensearchParamsConsumer;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Profile("!test")
@Component
@RequiredArgsConstructor
public class OpensearchStartup {

    @Value("${opensearch.index.personer}")
    private String personIndex;

    @Value("${opensearch.index.adresser}")
    private String adresserIndex;

    private final OpensearchParamsConsumer opensearchParamsConsumer;
    private final ObjectMapper objectMapper;
    private final OpenSearchClient openSearchClient;

    @PostConstruct
    public void opensearchStartup() {

        log.info("OpenSearch database oppdatering starter ...");

        updateIndexSetting(personIndex,
                "/config/person_settings.json",
                "/config/person_mapping.json")
                .then(updateIndexSetting(adresserIndex,
                        "/config/adresse_settings.json",
                        "/config/adresse_mapping.json"))
                .then(Mono.fromRunnable(() -> log.info("OpenSearch database oppdatering ferdig")))
                .block();
    }

    private Mono<Void> updateIndexSetting(String index, String settings, String mappings) {

        try {
            val existResponse = openSearchClient.indices().exists(i -> i.index(index));
            if (!existResponse.value()) {

                log.warn("Oppretter ny index: {}", index);

                val indexSetting = getJsonResource(settings);
                val indexMapping = getJsonResource(mappings);

                return opensearchParamsConsumer.oppdaterParametre(index, false, indexSetting)
                        .doOnNext(status -> log.info("OpenSearch opprettet index: {}, status: {}", index, status))
                        .then(opensearchParamsConsumer.oppdaterParametre(index, true, indexMapping))
                        .doOnNext(status -> log.info("OpenSearch satt mapping for index: {}, status {}", index, status))
                        .then();
            }

        } catch (IOException e) {

            log.error("Feilet å gjøre setting for index {}", index, e);
        }
        return Mono.empty();
    }

    public JsonNode getJsonResource(String pathResource) {

        val resource = new ClassPathResource(pathResource);
        try (val reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
            val contents = reader.lines().collect(Collectors.joining("\n"));
            return objectMapper.readTree(contents);

        } catch (IOException e) {
            log.error("Lesing av json ressurs {} feilet", pathResource, e);
            return null;
        }
    }
}