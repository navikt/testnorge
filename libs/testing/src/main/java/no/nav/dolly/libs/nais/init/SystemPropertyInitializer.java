package no.nav.dolly.libs.nais.init;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Slf4j
public class SystemPropertyInitializer implements Runnable {

    private final String systemProperty;
    private final String sourceFile;

    public void run() {
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
