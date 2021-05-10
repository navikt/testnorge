package no.nav.pdl.forvalter.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.Callable;

@Slf4j
public class TilfeldigLandCommand implements Callable<String> {

    @Override
    public String call() {

        var resource = new ClassPathResource("landkoder/landkoder.yaml");
        var landkoder = new Properties();

        try (final InputStream stream = resource.getInputStream()) {
            landkoder.load(stream);
        } catch (IOException e) {
            log.error("Lesing av landkoder feilet", e);
        }

        return landkoder.keySet().stream()
                .skip(new SecureRandom().nextInt(landkoder.size()))
                .map(String.class::cast)
                .findFirst()
                .orElse(null);
    }
}
