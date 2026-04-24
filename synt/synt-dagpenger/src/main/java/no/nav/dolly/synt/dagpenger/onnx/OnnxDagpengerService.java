package no.nav.dolly.synt.dagpenger.onnx;

import ai.onnxruntime.NodeInfo;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OnnxValue;
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
import org.springframework.stereotype.Service;

@Service
public class OnnxDagpengerService implements DagpengerInferenceService {

    private static final String MODEL_FILENAME_REGEX = ".*_(DAGO|PERM)_.*\\.onnx$";

    private final Path modelDir;
    private final Map<RettighetType, Path> modelByRettighet;
    private final OrtEnvironment environment;

    public OnnxDagpengerService(Path modelDirectory) {
        this.modelDir = modelDirectory;
        this.modelByRettighet = discoverOneModelPerRettighet(modelDir);
        this.environment = OrtEnvironment.getEnvironment();
    }

    @Override
    public OnnxPrediction infer(RettighetType rettighet, LocalDate vedtakDate) {
        Path modelPath = Optional.ofNullable(modelByRettighet.get(rettighet))
                .orElseThrow(() -> new IllegalArgumentException("No ONNX model found for rettighet " + rettighet));

        try (OrtSession session = environment.createSession(modelPath.toString(), new OrtSession.SessionOptions())) {
            String inputName = session.getInputNames().iterator().next();
            int featureCount = resolveFeatureCount(session, inputName);
            float[] featureVector = buildFeatureVector(vedtakDate, featureCount);

            try (OnnxTensor inputTensor = OnnxTensor.createTensor(environment, new float[][] {featureVector});
                 OrtSession.Result result = session.run(Map.of(inputName, inputTensor))) {

                String label = extractLabel(result).orElse("UNKNOWN");
                Map<String, Double> probabilities = extractProbabilities(result);
                double confidence = probabilities.isEmpty()
                        ? 0.0
                        : probabilities.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

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

    static float[] buildFeatureVector(LocalDate date, int featureCount) {
        float[] features = new float[Math.max(1, featureCount)];
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
        Map<RettighetType, List<Path>> grouped = new EnumMap<>(RettighetType.class);

        try (var stream = Files.list(modelDirectory)) {
            List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().matches(MODEL_FILENAME_REGEX))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();

            for (Path file : files) {
                String name = file.getFileName().toString().toUpperCase(Locale.ROOT);
                for (RettighetType rettighet : RettighetType.values()) {
                    if (name.contains("_" + rettighet.name() + "_")) {
                        grouped.computeIfAbsent(rettighet, ignored -> new ArrayList<>()).add(file);
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to scan ONNX model directory: " + modelDirectory, e);
        }

        Map<RettighetType, Path> selected = new EnumMap<>(RettighetType.class);
        for (RettighetType rettighet : RettighetType.values()) {
            List<Path> candidates = grouped.getOrDefault(rettighet, List.of());
            if (!candidates.isEmpty()) {
                selected.put(rettighet, candidates.getFirst());
            }
        }

        if (selected.isEmpty()) {
            throw new IllegalStateException("No ONNX models matched regex in directory: " + modelDirectory);
        }

        return selected;
    }

    private static int resolveFeatureCount(OrtSession session, String inputName) throws OrtException {
        NodeInfo input = Objects.requireNonNull(session.getInputInfo().get(inputName), "Missing ONNX input metadata");
        TensorInfo tensorInfo = (TensorInfo) input.getInfo();
        long[] shape = tensorInfo.getShape();

        if (shape.length >= 2 && shape[1] > 0) {
            return (int) shape[1];
        }
        return 1;
    }

    private static Optional<String> extractLabel(OrtSession.Result result) throws OrtException {
        if (!result.iterator().hasNext()) {
            return Optional.empty();
        }

        for (Map.Entry<String, OnnxValue> entry : result) {
            Object value = entry.getValue().getValue();
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

    private static Map<String, Double> extractProbabilities(OrtSession.Result result) throws OrtException {
        for (Map.Entry<String, OnnxValue> entry : result) {
            Object value = entry.getValue().getValue();

            if (value instanceof float[][] matrix && matrix.length > 0 && matrix[0].length > 0) {
                Map<String, Double> probs = new LinkedHashMap<>();
                for (int i = 0; i < matrix[0].length; i++) {
                    probs.put("class_" + i, (double) matrix[0][i]);
                }
                return probs;
            }

            if (value instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof Map<?, ?> map) {
                return map.entrySet().stream()
                        .filter(e -> e.getKey() != null && e.getValue() instanceof Number)
                        .collect(Collectors.toMap(
                                e -> e.getKey().toString(),
                                e -> ((Number) e.getValue()).doubleValue(),
                                (a, b) -> a,
                                LinkedHashMap::new
                        ));
            }

            if (value instanceof Map<?, ?> map) {
                return map.entrySet().stream()
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

    @Override
    public Path getModelDir() {
        return modelDir;
    }

    @Override
    public Map<RettighetType, String> getSelectedModels() {
        return modelByRettighet.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getFileName().toString()));
    }
}




