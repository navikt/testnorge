package no.nav.dolly.libs.nais;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class NaisFileIntoSystemPropertyInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final String systemProperty;
    private final String sourceFile;

    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {

        boolean isLocalProfile = Arrays
                .stream(applicationContext.getEnvironment().getActiveProfiles())
                .anyMatch(s -> s.startsWith("local"));
        if (isLocalProfile) {
            log.info("Skipping setting property {} from file {} due to local profile", systemProperty, sourceFile);
            return;
        }
        try {
            var path = Paths.get(sourceFile);
            var value = Files.readString(path).trim();
            System.setProperty(systemProperty, value);
            log.info("System property {} set from file {}", systemProperty, sourceFile);
        } catch (Exception e) {
            throw new RuntimeException("Error setting system property %s from file %s".formatted(systemProperty, sourceFile), e);
        }

    }
}
