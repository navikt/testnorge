package no.nav.pdl.forvalter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@Service
public class EmbeteService {

    private static final Properties embeter = new Properties();

    static {
        var resource = new ClassPathResource("vergemaal/embeter.yaml");

        try (final InputStream stream = resource.getInputStream()) {
            embeter.load(stream);
        } catch (IOException e) {
            log.error("Lesing av landkoder feilet", e);
        }
    }

    public String getNavn(String embete) {
        return (String) embeter.getOrDefault(embete, embete);
    }
}
