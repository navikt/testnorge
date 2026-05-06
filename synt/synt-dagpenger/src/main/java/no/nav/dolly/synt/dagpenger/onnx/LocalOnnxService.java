package no.nav.dolly.synt.dagpenger.onnx;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Profile({
        "local",
        "test"}
)
@Slf4j
class LocalOnnxService implements OnnxService {

    private final DagpengerGeneratorService dagpengerGeneratorService = new DagpengerGeneratorService(modelDirectoryFromClasspath());

    private static Path modelDirectoryFromClasspath() {

        try {
            var target = extractModelsToTemporaryDirectory();
            log.info("Extracted ONNX models to {}", target);
            return target;
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to extract ONNX models from classpath", e);
        }

    }

    private static Path extractModelsToTemporaryDirectory()
            throws IOException, IllegalStateException {

        var targetDir = Files.createTempDirectory("synt-dagpenger-models-");
        targetDir.toFile().deleteOnExit();

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
        return targetDir;

    }

    @Override
    public List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates) {

        var rettighetType = RettighetType.valueOf(rettighet.toUpperCase(Locale.ROOT));
        return dagpengerGeneratorService.generateVedtak(rettighetType, startDates)
                .stream()
                .map(VedtakMapper::fromPrediction)
                .collect(Collectors.toList());

    }

}
