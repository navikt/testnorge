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
public class TilfeldigLandService {

    private static final Properties landkoder = new Properties();

    static {
        var resource = new ClassPathResource("landkoder/landkoder.yaml");

        try (final InputStream stream = resource.getInputStream()) {
            landkoder.load(stream);
        } catch (IOException e) {
            log.error("Lesing av landkoder feilet", e);
        }
    }

    public String getLand() {
        return landkoder.keySet().stream()
                .skip(new SecureRandom().nextInt(landkoder.size()))
                .map(String.class::cast)
                .findFirst()
                .orElse(null);
    }
}
