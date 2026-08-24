package no.nav.dolly.synt.tilleggsstonad.models;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
@Slf4j
public class LocalModels {

    public static Path get() {

        try {
            var target = extractModelsToTemporaryDirectory();
            log.info("Extracted legacy model metadata to {}", target);
            return target;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to extract legacy model metadata from classpath", e);
        }

    }

    private static Path extractModelsToTemporaryDirectory()
            throws IOException, IllegalStateException {

        var targetDir = Files.createTempDirectory("synt-tilleggsstonad-models-");
        targetDir.toFile().deleteOnExit();

        var resolver = new PathMatchingResourcePatternResolver();
        var resources = resolver.getResources("classpath*:models/*.json.gz");
        if (resources.length == 0) {
            throw new IllegalStateException("No legacy model metadata files found at classpath:models/*.json.gz");
        }
        for (var resource : resources) {
            var filename = resource.getFilename();
            if (filename == null) {
                continue;
            }
            try (var in = resource.getInputStream()) {
                Files.copy(in, targetDir.resolve(filename));
            }
            log.debug("Extracted model: {}", filename);
        }
        return targetDir;

    }

}
