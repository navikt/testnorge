package no.nav.dolly.synt.dagpenger.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
class ClasspathModelDirectory {

    private static final Logger log = LoggerFactory.getLogger(ClasspathModelDirectory.class);

    @Bean
    Path modelDirectory() {
        try {
            Path tempDir = Files.createTempDirectory("synt-dagpenger-models-");
            tempDir.toFile().deleteOnExit();
            extractModelsTo(tempDir);
            log.info("Extracted ONNX models to {}", tempDir);
            return tempDir;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to extract ONNX models from classpath", e);
        }
    }

    private void extractModelsTo(Path targetDir) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:models/*.onnx");
        if (resources.length == 0) {
            throw new IllegalStateException("No ONNX model files found at classpath:models/*.onnx");
        }
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null) {
                continue;
            }
            Path destination = targetDir.resolve(filename);
            try (InputStream in = resource.getInputStream()) {
                Files.copy(in, destination);
            }
            log.debug("Extracted model: {}", filename);
        }
    }
}

