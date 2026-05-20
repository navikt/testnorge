package no.nav.dolly.synt.dagpenger.onnx;

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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class DagpengevedtakGenerator {

    private static final String MODEL_FILENAME_REGEX = ".*_(DAGO|PERM)_.*\\.onnx$";

    private final Map<RettighetType, Path> modelByRettighet;
    private final OrtEnvironment environment = OrtEnvironment.getEnvironment();
    private final Map<RettighetType, OrtSession> sessionByRettighet = new EnumMap<>(RettighetType.class);

    DagpengevedtakGenerator(Path modelDirectory) {
        this.modelByRettighet = discoverOneModelPerRettighet(modelDirectory);
    }

    List<GeneratedDagpengevedtak> generateVedtak(RettighetType rettighet, List<String> vedtakStartDatoer) {

        return vedtakStartDatoer
                .stream()
                .map(LocalDate::parse)
                .map(date -> toVedtak(rettighet, date))
                .toList();

    }

    private GeneratedDagpengevedtak toVedtak(RettighetType rettighet, LocalDate date) {

        var prediction = infer(rettighet, date);
        return new GeneratedDagpengevedtak(
                rettighet.name(),
                "GENERERT_ONNX",
                date.toString(),
                date.toString(),
                prediction.modelFile(),
                prediction.predictedLabel(),
                prediction.confidence(),
                prediction.probabilities()
        );

    }

    private OnnxPrediction infer(RettighetType rettighet, LocalDate vedtakDate) {

        var modelPath = Optional
                .ofNullable(modelByRettighet.get(rettighet))
                .orElseThrow(() -> new IllegalArgumentException("No ONNX model found for rettighet " + rettighet));
        var session = getOrCreateSession(rettighet, modelPath);

        try {
            var inputName = session
                    .getInputNames()
                    .iterator()
                    .next();
            var featureCount = resolveFeatureCount(session, inputName);
            var featureVector = buildFeatureVector(vedtakDate, featureCount);

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

    private OrtSession getOrCreateSession(RettighetType rettighet, Path modelPath) {
        return sessionByRettighet
                .computeIfAbsent(rettighet, ignored -> {
                    try {
                        return environment.createSession(modelPath.toString(), new OrtSession.SessionOptions());
                    } catch (OrtException e) {
                        throw new IllegalStateException("Failed to create ONNX session for " + rettighet, e);
                    }
                });
    }

    private static float[] buildFeatureVector(LocalDate date, int featureCount) {

        var features = new float[Math.max(1, featureCount)];
        features[0] = date.getYear();
        if (features.length > 1) {
            features[1] = date.getMonthValue();
        }
        if (features.length > 2) {
            features[2] = date.getDayOfMonth();
        }
        for (int i = 3; i < features.length; i++) {
            features[i] = 0.0f;
        }
        return features;

    }

    private static Map<RettighetType, Path> discoverOneModelPerRettighet(Path modelDirectory) {

        var grouped = new EnumMap<RettighetType, List<Path>>(RettighetType.class);
        try (var stream = Files.list(modelDirectory)) {

            var files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches(MODEL_FILENAME_REGEX))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();
            for (var file : files) {
                var name = file.getFileName().toString().toUpperCase(Locale.ROOT);
                for (var rettighet : RettighetType.values()) {
                    if (name.contains("_" + rettighet.name() + "_")) {
                        grouped.computeIfAbsent(rettighet, ignored -> new ArrayList<>()).add(file);
                    }
                }
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan ONNX model directory: " + modelDirectory, e);
        }

        var selected = new EnumMap<RettighetType, Path>(RettighetType.class);
        for (var rettighet : RettighetType.values()) {
            var candidates = grouped.getOrDefault(rettighet, List.of());
            if (!candidates.isEmpty()) {
                selected.put(rettighet, candidates.getFirst());
            }
        }
        if (selected.isEmpty()) {
            throw new IllegalStateException("No ONNX models matched regex in directory: " + modelDirectory);
        }
        return selected;

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
                                (a, b) -> a,
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
                                (a, b) -> a,
                                LinkedHashMap::new
                        ));
            }
        }

        return new HashMap<>();

    }
}


