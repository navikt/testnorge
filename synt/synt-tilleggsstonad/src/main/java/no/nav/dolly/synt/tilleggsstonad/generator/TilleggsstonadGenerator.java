package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

@Slf4j
public class TilleggsstonadGenerator {

    static final String MODEL_FILE_PREFIX = "tilleggsstonad_bean_";
    static final String MODEL_FILE_SUFFIX = ".json.gz";
    static final int MAX_ATTEMPTS = 10_000;

    private final Map<TilleggsstonadType, TilleggsstonadModel> models;

    TilleggsstonadGenerator(Map<TilleggsstonadType, TilleggsstonadModel> models) {
        this.models = Collections.unmodifiableMap(new EnumMap<>(models));
    }

    public static TilleggsstonadGenerator fromDirectory(Path directory) {

        var started = Instant.now();
        var models = new EnumMap<TilleggsstonadType, TilleggsstonadModel>(TilleggsstonadType.class);
        for (var type : TilleggsstonadType.values()) {
            var path = directory.resolve(modelFileName(type));
            if (!Files.isRegularFile(path)) {
                throw new IllegalStateException("Missing tilleggsstonad model file " + path);
            }
            models.put(type, TilleggsstonadModel.read(path));
        }
        log.info("Loaded {} tilleggsstonad model(s) from {} in {}ms", models.size(), directory,
                Duration.between(started, Instant.now()).toMillis());
        return new TilleggsstonadGenerator(models);

    }

    static String modelFileName(TilleggsstonadType type) {
        return MODEL_FILE_PREFIX + type.getModelName() + MODEL_FILE_SUFFIX;
    }

    public List<Map<String, Object>> generateVedtak(TilleggsstonadType type, List<VedtakRequestDto> requests,
                                                    boolean brukInnsendtTilDato, RandomGenerator random) {

        var rule = type.getMaalgruppeRule();
        return switch (rule.mode()) {
            case RETRY -> generateUntilValidMaalgruppe(type, requests, brukInnsendtTilDato, random);
            case REPLACE -> {
                var vedtaksliste = generateOnce(type, requests, brukInnsendtTilDato, random);
                rule.replaceInvalid(vedtaksliste, random);
                yield vedtaksliste;
            }
            case NONE -> generateOnce(type, requests, brukInnsendtTilDato, random);
        };

    }

    private List<Map<String, Object>> generateUntilValidMaalgruppe(TilleggsstonadType type, List<VedtakRequestDto> requests,
                                                                   boolean brukInnsendtTilDato, RandomGenerator random) {

        if (requests.isEmpty()) {
            throw new IllegalArgumentException("Forespørselen må inneholde minst ett vedtak for " + type);
        }
        var rule = type.getMaalgruppeRule();
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            var vedtaksliste = generateOnce(type, requests, brukInnsendtTilDato, random);
            if (rule.isValid(vedtaksliste)) {
                if (attempt > 1) {
                    log.debug("Found valid maalgruppe for {} after {} attempts", type, attempt);
                }
                return vedtaksliste;
            }
        }
        throw new IllegalStateException("Fant ingen gyldig målgruppe for %s etter %d forsøk".formatted(type, MAX_ATTEMPTS));

    }

    List<Map<String, Object>> generateOnce(TilleggsstonadType type, List<VedtakRequestDto> requests,
                                           boolean brukInnsendtTilDato, RandomGenerator random) {

        var model = models.get(type);
        var historikk = HistorySynthesizer.synthesize(model, type.getHistorySynthesis(), requests, random);
        for (int index = 0; index < historikk.size(); index++) {
            var request = requests.get(index);
            VedtakPostprocessor.convertRelativeDates(historikk.get(index), request.getFraDato(), request.getTilDato(), model,
                    type.getDifferentTimeFormats(), brukInnsendtTilDato);
        }
        var vedtaksliste = new ArrayList<Map<String, Object>>(historikk.size());
        for (var vedtak : historikk) {
            vedtaksliste.add(VedtakPostprocessor.structure(vedtak, type, random));
        }
        return vedtaksliste;

    }

}
