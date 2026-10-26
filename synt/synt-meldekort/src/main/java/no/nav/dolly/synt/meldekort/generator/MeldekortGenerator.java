package no.nav.dolly.synt.meldekort.generator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.meldekort.bean.BeanModelReader;
import no.nav.dolly.synt.meldekort.bean.BeanSynthesizer;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

@Slf4j
class MeldekortGenerator {

    static final Pattern MODEL_FILENAME = Pattern.compile("meldekort_bean_([A-Z]+)\\.json\\.gz");

    private record MeldekortModel(BeanSynthesizer synthesizer, MeldekortLayout layout) {
    }

    private final Map<MeldekortType, MeldekortModel> models;

    MeldekortGenerator(Map<MeldekortType, BeanSynthesizer> synthesizers) {

        var missing = Arrays.stream(MeldekortType.values())
                .filter(type -> !synthesizers.containsKey(type))
                .toList();
        if (!missing.isEmpty()) {
            throw new IllegalStateException("Missing meldekort model(s) for " + missing);
        }
        var loaded = new EnumMap<MeldekortType, MeldekortModel>(MeldekortType.class);
        synthesizers.forEach((type, synthesizer) -> {
            try {
                loaded.put(type, new MeldekortModel(synthesizer, new MeldekortLayout(synthesizer.columnNames())));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("Invalid meldekort model for %s: %s".formatted(type, e.getMessage()), e);
            }
        });
        this.models = Map.copyOf(loaded);

    }

    static MeldekortGenerator fromDirectory(Path modelDirectory) {

        var synthesizers = new EnumMap<MeldekortType, BeanSynthesizer>(MeldekortType.class);
        for (var file : modelFiles(modelDirectory)) {
            var matcher = MODEL_FILENAME.matcher(file.getFileName().toString());
            if (!matcher.matches()) {
                continue;
            }
            var meldegruppe = matcher.group(1);
            var type = Arrays.stream(MeldekortType.values())
                    .filter(candidate -> candidate.name().equals(meldegruppe))
                    .findFirst();
            if (type.isEmpty()) {
                log.info("Skipping model {} because meldegruppe {} is not supported by the API", file.getFileName(), meldegruppe);
                continue;
            }
            var synthesizer = BeanModelReader.read(file);
            if (!meldegruppe.equals(synthesizer.meldegruppe())) {
                throw new IllegalStateException("Model file %s contains meldegruppe %s"
                        .formatted(file.getFileName(), synthesizer.meldegruppe()));
            }
            synthesizers.put(type.get(), synthesizer);
            log.info("Loaded meldekort model {} with {} columns", meldegruppe, synthesizer.columnNames().size());
        }
        return new MeldekortGenerator(synthesizers);

    }

    List<String> generateMeldekort(MeldekortType meldekortType, int antallMeldekort, Double arbeidstimerOverride, RandomGenerator random) {

        var model = models.get(meldekortType);
        if (isNull(model)) {
            throw new IllegalStateException("No meldekort model for " + meldekortType);
        }
        var generated = new ArrayList<String>(antallMeldekort);
        for (int index = 0; index < antallMeldekort; index++) {
            var row = model.synthesizer().synthesizeRow(index, random);
            var meldekort = MeldekortMapper.fromRow(model.layout(), row, meldekortType);
            generated.add(MeldekortXmlMapper.toXml(index, meldekort, arbeidstimerOverride));
        }
        return generated;

    }

    private static List<Path> modelFiles(Path modelDirectory) {

        try (var files = Files.list(modelDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to list model directory " + modelDirectory, e);
        }

    }

}
