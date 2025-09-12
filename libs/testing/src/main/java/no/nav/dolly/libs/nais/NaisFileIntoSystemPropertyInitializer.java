package no.nav.dolly.libs.nais;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Slf4j
public class NaisFileIntoSystemPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final String systemProperty;
    private final String sourceFile;

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {

        try {
            var path = Paths.get(sourceFile);
            if (Files.exists(path)) {
                var value = Files.readString(path).trim();
                System.setProperty(systemProperty, value);
                log.info("System property {} set from file {}", systemProperty, sourceFile);
            } else {
                log.warn("File not found at {}; hopefully you're running locally", sourceFile);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error setting system property %s from file %s".formatted(systemProperty, sourceFile), e);
        }

    }
}
