package no.nav.dolly.synt.meldekort.onnx;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class MeldekortGenerator {

    private static final String MODEL_FILENAME_REGEX = ".*_(ARBS|ATTF|DAGP|INDIV|FY|DAGO|PERM|LONN)_.*\\.onnx$";
    private static final int PREDICTIONS_PER_MELDEKORT = 3;

    private final Map<String, List<Path>> modelByRoutingKey;
    private final List<Path> allModels;
    private final OrtEnvironment environment = OrtEnvironment.getEnvironment();
    private final Map<Path, OrtSession> sessionByModel = new HashMap<>();

    MeldekortGenerator(Path modelDirectory) {
        this.modelByRoutingKey = discoverModels(modelDirectory);
        this.allModels = modelByRoutingKey
                .values()
                .stream()
                .flatMap(List::stream)
                .distinct()
                .toList();
    }

    List<String> generateMeldekort(MeldekortType meldekortType, int antallMeldekort, Double arbeidstimerOverride) {

        var generated = new ArrayList<String>();
        var candidates = resolveCandidates(meldekortType);
        for (int index = 0; index < antallMeldekort; index++) {
            var predictions = inferForMeldekort(candidates, meldekortType, index);
            var meldekort = MeldekortMapper.fromPredictions(meldekortType, index, predictions, arbeidstimerOverride);
            generated.add(MeldekortXmlMapper.toXml(index, meldekort));
        }
        return generated;

    }

    private List<OnnxPrediction> inferForMeldekort(List<Path> candidates, MeldekortType meldekortType, int index) {

        var predictions = new ArrayList<OnnxPrediction>(PREDICTIONS_PER_MELDEKORT);
        var modelType = MeldekortModelType.forMeldegruppe(meldekortType);
        for (int slot = 0; slot < PREDICTIONS_PER_MELDEKORT; slot++) {
            var modelPath = candidates.get(Math.floorMod(index + (slot * 7), candidates.size()));
            predictions.add(infer(modelPath, modelType, index, slot));
        }
        return predictions;

    }

    private OnnxPrediction infer(Path modelPath, MeldekortModelType modelType, int index, int slot) {

        var session = getOrCreateSession(modelPath);

        try {
            var inputName = session
                    .getInputNames()
                    .iterator()
                    .next();
            var featureCount = resolveFeatureCount(session, inputName);
            var featureVector = buildFeatureVector(modelType, index, slot, featureCount);

            try (
                    var inputTensor = OnnxTensor.createTensor(environment, new float[][]{featureVector});
                    var result = session.run(Map.of(inputName, inputTensor))
            ) {

                var label = extractLabel(result).orElse("UNKNOWN");
                var probabilities = extractProbabilities(result);
                var confidence = probabilities.isEmpty()
                        ? 0.0
                        : probabilities
                        .values()
                        .stream()
                        .mapToDouble(Double::doubleValue)
                        .max()
                        .orElse(0.0);
                return new OnnxPrediction(
                        modelPath.getFileName().toString(),
                        label,
                        confidence,
                        probabilities
                );

            }

        } catch (OrtException e) {
            throw new IllegalStateException("ONNX inference failed for model " + modelPath, e);
        }

    }

    private OrtSession getOrCreateSession(Path modelPath) {
        return sessionByModel
                .computeIfAbsent(modelPath, ignored -> {
                    try {
                        return environment.createSession(modelPath.toString(), new OrtSession.SessionOptions());
                    } catch (OrtException e) {
                        throw new IllegalStateException("Failed to create ONNX session for model " + modelPath, e);
                    }
                });
    }

    private List<Path> resolveCandidates(MeldekortType meldekortType) {

        var directCandidates = modelByRoutingKey.getOrDefault(meldekortType.name(), List.of());
        if (!directCandidates.isEmpty()) {
            return directCandidates;
        }

        var modelType = MeldekortModelType.forMeldegruppe(meldekortType);
        var fallbackCandidates = modelByRoutingKey.getOrDefault(modelType.name(), List.of());
        if (!fallbackCandidates.isEmpty()) {
            return fallbackCandidates;
        }

        if (allModels.isEmpty()) {
            throw new IllegalStateException("No ONNX model files available");
        }
        return allModels;

    }

    private static float[] buildFeatureVector(MeldekortModelType modelType, int index, int slot, int featureCount) {

        var today = LocalDate.now().minusDays(index);
        var deterministicNoise = Math.floorMod(Objects.hash(modelType.name(), index, slot), 1000) / 1000.0f;
        var features = new float[Math.max(1, featureCount)];
        features[0] = today.getYear();
        if (features.length > 1) {
            features[1] = today.getMonthValue();
        }
        if (features.length > 2) {
            features[2] = today.getDayOfMonth();
        }
        if (features.length > 3) {
            features[3] = index;
        }
        if (features.length > 4) {
            features[4] = modelType.ordinal();
        }
        if (features.length > 5) {
            features[5] = slot;
        }
        for (int i = 6; i < features.length; i++) {
            features[i] = deterministicNoise + ((float) ((i % 5) * 0.01));
        }
        return features;

    }

    private static Map<String, List<Path>> discoverModels(Path modelDirectory) {

        var grouped = new HashMap<String, List<Path>>();
        try (var stream = Files.list(modelDirectory)) {

            var files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches(MODEL_FILENAME_REGEX))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
            for (var file : files) {
                var name = file.getFileName().toString().toUpperCase(Locale.ROOT);
                for (var routingKey : extractRoutingKeys(name)) {
                    grouped.computeIfAbsent(routingKey, ignored -> new ArrayList<>()).add(file);
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan ONNX model directory: " + modelDirectory, e);
        }

        if (grouped.isEmpty()) {
            throw new IllegalStateException("No ONNX models matched regex in directory: " + modelDirectory);
        }
        grouped.replaceAll((ignored, value) -> Collections.unmodifiableList(value));
        return grouped;

    }

    private static Set<String> extractRoutingKeys(String filenameUpperCase) {

        var keys = EnumSet.noneOf(RoutingToken.class);
        for (var token : RoutingToken.values()) {
            if (filenameUpperCase.contains("_" + token.name() + "_")) {
                keys.add(token);
            }
        }
        return keys.stream().map(Enum::name).collect(Collectors.toSet());

    }

    private enum RoutingToken {
        ARBS,
        ATTF,
        DAGP,
        INDIV,
        FY,
        DAGO,
        PERM,
        LONN
    }

    private static int resolveFeatureCount(OrtSession session, String inputName)
            throws OrtException {

        var input = Objects.requireNonNull(session.getInputInfo().get(inputName), "Missing ONNX input metadata");
        var tensorInfo = (TensorInfo) input.getInfo();
        var shape = tensorInfo.getShape();
        if (shape.length >= 2 && shape[1] > 0) {
            return (int) shape[1];
        }
        return 1;

    }

    private static Optional<String> extractLabel(OrtSession.Result result)
            throws OrtException {

        if (!result.iterator().hasNext()) {
            return Optional.empty();
        }
        for (var entry : result) {
            var value = entry.getValue().getValue();
            if (value instanceof long[][] longMatrix && longMatrix.length > 0 && longMatrix[0].length > 0) {
                return Optional.of(Long.toString(longMatrix[0][0]));
            }
            if (value instanceof float[][] floatMatrix && floatMatrix.length > 0 && floatMatrix[0].length > 0) {
                return Optional.of(Float.toString(floatMatrix[0][0]));
            }
            if (value instanceof String[] labels && labels.length > 0) {
                return Optional.of(labels[0]);
            }
            if (value instanceof long[] longLabels && longLabels.length > 0) {
                return Optional.of(Long.toString(longLabels[0]));
            }
        }
        return Optional.empty();

    }

    private static Map<String, Double> extractProbabilities(OrtSession.Result result)
            throws OrtException {

        for (var entry : result) {

            var value = entry.getValue().getValue();
            if (value instanceof float[][] matrix && matrix.length > 0 && matrix[0].length > 0) {
                var probs = new LinkedHashMap<String, Double>();
                for (int i = 0; i < matrix[0].length; i++) {
                    probs.put("class_" + i, (double) matrix[0][i]);
                }
                return probs;
            }

            if (value instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof Map<?, ?> map) {
                return map
                        .entrySet()
                        .stream()
                        .filter(e -> e.getKey() != null && e.getValue() instanceof Number)
                        .collect(Collectors.toMap(
                                e -> e.getKey().toString(),
                                e -> ((Number) e.getValue()).doubleValue(),
                                (existing, ignored) -> existing,
                                LinkedHashMap::new
                        ));
            }

            if (value instanceof Map<?, ?> map) {
                return map
                        .entrySet()
                        .stream()
                        .filter(e -> e.getKey() != null && e.getValue() instanceof Number)
                        .collect(Collectors.toMap(
                                e -> e.getKey().toString(),
                                e -> ((Number) e.getValue()).doubleValue(),
                                (existing, ignored) -> existing,
                                LinkedHashMap::new
                        ));
            }
        }

        return new HashMap<>();

    }

}

