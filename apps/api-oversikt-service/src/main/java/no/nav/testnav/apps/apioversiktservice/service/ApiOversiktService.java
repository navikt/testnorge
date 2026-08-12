package no.nav.testnav.apps.apioversiktservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiOversiktService {

    private static final String PATH_RESOURCE = "data/apioversikt.yml";
    private final JsonMapper jsonMapper;

    public Mono<JsonNode> getDokumeter() {

        try {
            var resource = new ClassPathResource(PATH_RESOURCE);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
                var yaml = reader.lines().collect(Collectors.joining("\n"));

                var maps = new Yaml().load(yaml);
                var jsonString = jsonMapper.writeValueAsString(maps);
                return Mono.just(jsonMapper.readTree(jsonString));
            }
        } catch (Exception e) {
            log.error("Lesing av query ressurs {} feilet", PATH_RESOURCE, e);
            return Mono.error(e);
        }
    }
}