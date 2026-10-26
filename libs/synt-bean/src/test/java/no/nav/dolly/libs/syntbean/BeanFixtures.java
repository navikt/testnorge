package no.nav.dolly.libs.syntbean;

import lombok.experimental.UtilityClass;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import static java.util.Objects.requireNonNull;

@UtilityClass
class BeanFixtures {

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    static BeanSynthesizer synthesizer(String fixture) {

        try (var input = open("bean/meldekort_bean_" + fixture + ".json.gz")) {
            return BeanModelReader.read(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    static BeanModelDocument document(String fixture) {

        try (var input = open("bean/meldekort_bean_" + fixture + ".json.gz")) {
            return BeanModelReader.readDocument(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    static JsonNode reference(String fixture) {

        try (var input = open("bean/reference_" + fixture + ".json")) {
            return JSON_MAPPER.readTree(input);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    static InputStream open(String resource) {
        return requireNonNull(BeanFixtures.class.getClassLoader().getResourceAsStream(resource), resource);
    }

}
