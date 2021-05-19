package no.nav.pdl.forvalter.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;

@Slf4j
@Service
public class TilfeldigKommuneService {

    private static final Properties kommuner = new Properties();

    static {
        var resource = new ClassPathResource("kommuner/kommuner.yaml");

        try (final InputStream stream = resource.getInputStream()) {
            kommuner.load(stream);
        } catch (IOException e) {
            log.error("Lesing av kommuner feilet", e);
        }
    }

    public String getKommune() {
        return kommuner.keySet().stream()
                .skip(new SecureRandom().nextInt(kommuner.size()))
                .map(String.class::cast)
                .findFirst()
                .orElse(null);
    }
}
