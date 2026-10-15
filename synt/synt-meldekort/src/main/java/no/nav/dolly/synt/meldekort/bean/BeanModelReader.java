package no.nav.dolly.synt.meldekort.bean;

import lombok.experimental.UtilityClass;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

@UtilityClass
public class BeanModelReader {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public static BeanSynthesizer read(Path path) {

        try (var input = Files.newInputStream(path)) {
            return read(input);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read bean model " + path, e);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Invalid bean model " + path + ": " + e.getMessage(), e);
        }

    }

    public static BeanSynthesizer read(InputStream input)
            throws IOException {

        return new BeanSynthesizer(readDocument(input));

    }

    static BeanModelDocument readDocument(InputStream input)
            throws IOException {

        try (var gzip = new GZIPInputStream(input)) {
            return JSON_MAPPER.readValue(gzip, BeanModelDocument.class);
        }

    }

}
