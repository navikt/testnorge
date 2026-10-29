package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.Getter;
import no.nav.dolly.libs.syntbean.BeanSynthesizer;
import no.nav.dolly.libs.syntbean.KernelDensitySampler;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
class TilleggsstonadModel {

    static final int FORMAT_VERSION = 1;

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private final String name;
    private final String root;
    private final List<String> deltaColumns;
    private final Map<String, Map<String, BeanSynthesizer>> synthesizers;
    private final Map<String, KernelDensitySampler> kernelDensities;

    TilleggsstonadModel(TilleggsstonadModelDocument document) {

        if (isNull(document) || document.formatVersion() != FORMAT_VERSION) {
            throw new IllegalArgumentException("Unsupported tilleggsstonad model format version, expected " + FORMAT_VERSION);
        }
        if (isNull(document.root()) || isNull(document.deltaColumns()) || isNull(document.beanModels()) || document.beanModels().isEmpty()) {
            throw new IllegalArgumentException("Tilleggsstonad model %s is missing root, delta columns or bean models".formatted(document.model()));
        }
        this.name = document.model();
        this.root = document.root();
        this.deltaColumns = List.copyOf(document.deltaColumns());
        var synthesizersByVedtaktype = new LinkedHashMap<String, Map<String, BeanSynthesizer>>();
        document.beanModels().forEach((vedtaktype, byUtfall) -> {
            var synthesizersByUtfall = new LinkedHashMap<String, BeanSynthesizer>();
            byUtfall.forEach((utfall, beanModel) -> synthesizersByUtfall.put(utfall, new BeanSynthesizer(beanModel)));
            synthesizersByVedtaktype.put(vedtaktype, Collections.unmodifiableMap(synthesizersByUtfall));
        });
        this.synthesizers = Collections.unmodifiableMap(synthesizersByVedtaktype);
        var densities = new LinkedHashMap<String, KernelDensitySampler>();
        if (nonNull(document.kde())) {
            document.kde().forEach((column, kde) -> densities.put(column, new KernelDensitySampler(kde)));
        }
        this.kernelDensities = Collections.unmodifiableMap(densities);

    }

    static TilleggsstonadModel read(Path path) {

        try (var input = new GZIPInputStream(Files.newInputStream(path))) {
            return new TilleggsstonadModel(JSON_MAPPER.readValue(input, TilleggsstonadModelDocument.class));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read tilleggsstonad model " + path, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Invalid tilleggsstonad model " + path + ": " + e.getMessage(), e);
        }

    }

    BeanSynthesizer synthesizer(String vedtaktype, String utfall) {

        var byUtfall = synthesizers.get(vedtaktype);
        if (isNull(byUtfall) || !byUtfall.containsKey(utfall)) {
            throw new IllegalArgumentException("Modellen %s har ingen modell for vedtaktype %s med utfall %s"
                    .formatted(name, vedtaktype, utfall));
        }
        return byUtfall.get(utfall);

    }

}
