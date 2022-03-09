package no.nav.dolly.consumer.graphql;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Builder
public record GraphQLRequest(String query, Map<String, Object> variables) {

    public static String getQueryFromFile(String pathResource) {

        val resource = new ClassPathResource(pathResource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", pathResource, e);
            return null;
        }
    }
}