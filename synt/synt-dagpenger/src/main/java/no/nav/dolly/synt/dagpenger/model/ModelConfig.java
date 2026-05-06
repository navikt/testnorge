package no.nav.dolly.synt.dagpenger.model;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.dagpenger.onnx.DagpengerGeneratorBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@Slf4j
class ModelConfig {

    @Value("${app.config.bucket:}")
    private String bucket;

    @Bean
    @Profile("!local")
    ModelService bucketService(DagpengerGeneratorBean dagpengerGeneratorBean) {
        return new GoogleModelService(bucket, dagpengerGeneratorBean);
    }

    @Bean
    @Profile("local")
    ModelService localModelService(DagpengerGeneratorBean dagpengerGeneratorBean) {
        return new LocalModelService(dagpengerGeneratorBean);
    }

    @Bean
    @Profile("local")
    Path modelDirectoryFromClasspath() {

        try {
            var tempDir = Files.createTempDirectory("synt-dagpenger-models-");
            tempDir.toFile().deleteOnExit();
            extractModelsTo(tempDir);
            log.info("Extracted ONNX models to {}", tempDir);
            return tempDir;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to extract ONNX models from classpath", e);
        }

    }

    private void extractModelsTo(Path targetDir)
            throws IOException, IllegalStateException {

        var resolver = new PathMatchingResourcePatternResolver();
        var resources = resolver.getResources("classpath*:models/*.onnx");
        if (resources.length == 0) {
            throw new IllegalStateException("No ONNX model files found at classpath:models/*.onnx");
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

    }

    @Bean
    @Profile("test")
    Path testModelDirectory() throws Exception {
        Path tempDir = Files.createTempDirectory("synt-dagpenger-test-models-");
        tempDir.toFile().deleteOnExit();
        return tempDir;
    }

}
