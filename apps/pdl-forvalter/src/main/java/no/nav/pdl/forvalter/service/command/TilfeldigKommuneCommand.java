package no.nav.pdl.forvalter.service.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.Properties;
import java.util.concurrent.Callable;

@Slf4j
public class TilfeldigKommuneCommand implements Callable<String> {

    @Override
    public String call() {

        var resource = new ClassPathResource("kommuner/kommuner.yaml");
        var kommuner = new Properties();

        try (final InputStream stream = resource.getInputStream()) {
            kommuner.load(stream);
        } catch (IOException e) {
            log.error("Lesing av kommuner feilet", e);
        }

        return kommuner.keySet().stream()
                .skip(new SecureRandom().nextInt(kommuner.size()))
                .map(String.class::cast)
                .findFirst()
                .orElse(null);
    }
}
