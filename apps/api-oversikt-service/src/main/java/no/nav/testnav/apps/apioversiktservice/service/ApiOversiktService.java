package no.nav.testnav.apps.apioversiktservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiOversiktService {

    private static final String PATH_RESOURCE = "data/apioversikt.yml";
    private final ObjectMapper objectMapper;

    public JsonNode getDokumeter() {

        var resource = new ClassPathResource(PATH_RESOURCE);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
            var yaml = reader.lines().collect(Collectors.joining("\n"));

            var maps = new Yaml().load(yaml);
            var jsonString = objectMapper.writeValueAsString(maps);
            return objectMapper.readTree(jsonString);

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", PATH_RESOURCE, e);
            return null;
        }
    }
}