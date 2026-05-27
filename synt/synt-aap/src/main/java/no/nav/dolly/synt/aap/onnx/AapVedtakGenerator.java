package no.nav.dolly.synt.aap.onnx;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.TensorInfo;
import no.nav.dolly.synt.aap.dto.VedtakRequestDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class AapVedtakGenerator {

    private static final Pattern MODEL_FILE_PATTERN = Pattern.compile("^(AAP|AAP_115|AATFOR|AAUNGUFOR|FRI_MK)_bean_model_([OESG])_(JA|NEI)_distributions_col_(\\d+)\\.onnx$");

    private static final Map<AapModelType, List<String>> COLUMN_ORDER = Map.of(
            AapModelType.AAP_115, List.of(
                    "VEDTAKTYPE", "VEDTAKSTATUS", "DATO_MOTTATT", "TIL_DATO", "UTFALL",
                    "AAARBEVNE", "AAMOTTSAMT", "AANAAVJOBB", "AANODVDOKU", "AANORSKFER",
                    "AASNARARBG", "AASNARTARB", "AATYPEJOBB", "INNTNEDS", "SYKSKADLYT",
                    "HOVED_TYPE", "HOVED_KLASSIFISERING", "HOVED_DIAGNOSE", "HOVED_KILDE", "HOVED_KILDEDATO",
                    "BI_TYPE", "BI_KLASSIFISERING", "BI_DIAGNOSE", "BI_KILDE", "BI_KILDEDATO"
            ),
            AapModelType.AATFOR, List.of(
                    "VEDTAKTYPE", "VEDTAKSTATUS", "DATO_MOTTATT", "UTFALL", "MISBRUK", "PSYK", "SKADE", "UTLANDSMOTTAKER"
            ),
            AapModelType.AAUNGUFOR, List.of(
                    "VEDTAKTYPE", "VEDTAKSTATUS", "FRA_DATO", "UTFALL", "AAUNGNEDS", "AANEDSSL", "AASSLDOK", "AAFOR36"
            ),
            AapModelType.FRI_MK, List.of(
                    "VEDTAKTYPE", "VEDTAKSTATUS", "UTFALL", "FRI_MAAI", "FRI_MABH", "FRI_MAPL", "FRI_MARY", "FRI_MATI", "FRI_MAVI", "FRI_MAVU", "DATO_MOTTATT"
            ),
            AapModelType.AAP, List.of()
    );

    private static final Set<String> BOOLEAN_FIELDS = Set.of(
            "MISBRUK", "PSYK", "SKADE", "AAUNGNEDS", "AANEDSSL", "AASSLDOK", "AAFOR36",
            "FRI_MAAI", "FRI_MABH", "FRI_MAPL", "FRI_MARY", "FRI_MATI", "FRI_MAVI", "FRI_MAVU",
            "AAARBEVNE", "AAMOTTSAMT", "AANAAVJOBB", "AANODVDOKU", "AANORSKFER", "AASNARARBG", "AASNARTARB", "AATYPEJOBB", "INNTNEDS", "SYKSKADLYT"
    );

    private static final Map<String, List<String>> AVBRUDDSKODER = Map.of(
            "MOTAT", List.of("FSOP"),
            "OPPRE", List.of("FSOP", "FFOV"),
            "REGIS", List.of("FSOP", "ESOP", "FFOV"),
            "INNST", List.of("EFOV", "FFAV")
    );

    private final OrtEnvironment environment = OrtEnvironment.getEnvironment();
    private final Random random = new Random();
    private final Map<Path, OrtSession> sessionCache = new HashMap<>();
    private final Map<OrtSession, Integer> featureCountCache = new HashMap<>();

    private final Map<AapModelType, Map<ModelSelector, List<DistributionModel>>> distributionsByType;

    AapVedtakGenerator(Path modelDirectory) {
        distributionsByType = discoverDistributionModels(modelDirectory);
    }

    List<Map<String, String>> generateVedtak(AapModelType modelType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return requests
                .stream()
                .map(request -> toVedtak(modelType, request, brukInnsendtTilDato))
                .toList();

    }

    private Map<String, String> toVedtak(AapModelType modelType, VedtakRequestDto request, boolean brukInnsendtTilDato) {

        var vedtak = new LinkedHashMap<String, String>();
        vedtak.put("FRA_DATO", request.getFraDato());
        vedtak.put("UTFALL", defaultText(request.getUtfall(), "JA"));
        vedtak.put("VEDTAKTYPE", defaultText(request.getVedtakTypeKode(), "O"));
        vedtak.put("DATO_MOTTATT", defaultText(request.getVedtakDato(), request.getFraDato()));
        vedtak.put("TIL_DATO", resolvedTilDato(request, brukInnsendtTilDato));

        var selector = new ModelSelector(vedtak.get("VEDTAKTYPE"), vedtak.get("UTFALL"));
        var bySelector = distributionsByType.getOrDefault(modelType, Map.of());
        var distributions = bySelector.getOrDefault(selector, List.of());
        var usedModelFiles = new ArrayList<String>();
        for (var distribution : distributions) {
            var prediction = infer(distribution.modelPath(), request);
            prediction.ifPresent(s -> vedtak.put(distribution.fieldName(), normalizePrediction(distribution.fieldName(), s)));
            usedModelFiles.add(distribution.fileName());
        }

        enrich(modelType, request, brukInnsendtTilDato, vedtak);
        if (!usedModelFiles.isEmpty()) {
            vedtak.put("_MODEL", usedModelFiles.stream().distinct().sorted().collect(Collectors.joining(",")));
        }
        return vedtak;

    }

    private void enrich(AapModelType modelType, VedtakRequestDto request, boolean brukInnsendtTilDato, Map<String, String> vedtak) {

        vedtak.put("FRA_DATO", request.getFraDato());
        vedtak.put("TIL_DATO", resolvedTilDato(request, brukInnsendtTilDato));
        vedtak.put("UTFALL", defaultText(request.getUtfall(), vedtak.getOrDefault("UTFALL", "JA")));
        vedtak.put("VEDTAKTYPE", defaultText(request.getVedtakTypeKode(), vedtak.getOrDefault("VEDTAKTYPE", "O")));
        vedtak.put("DATO_MOTTATT", defaultText(vedtak.get("DATO_MOTTATT"), defaultText(request.getVedtakDato(), request.getFraDato())));

        switch (modelType) {
            case AAP -> {
                vedtak.put("VEDTAK_DATO", defaultText(request.getVedtakDato(), request.getFraDato()));
                vedtak.put("AKTFASEKODE", defaultText(vedtak.get("AKTFASEKODE"), "UA"));
            }
            case AAP_115 -> {
                vedtak.put("HOVED_KILDEDATO", defaultText(vedtak.get("HOVED_KILDEDATO"), request.getFraDato()));
                vedtak.put("BI_KILDEDATO", defaultText(vedtak.get("BI_KILDEDATO"), request.getFraDato()));
            }
            case AATFOR, AAUNGUFOR, FRI_MK -> {
                var vedtakStatus = defaultText(vedtak.get("VEDTAKSTATUS"), "OPPRE");
                var avbruddskode = AVBRUDDSKODER
                        .getOrDefault(vedtakStatus, List.of())
                        .stream()
                        .findFirst()
                        .orElse("");
                vedtak.put("AVVBRUDDSKODE", avbruddskode);
            }
        }

    }

    private String resolvedTilDato(VedtakRequestDto request, boolean brukInnsendtTilDato) {
        if (brukInnsendtTilDato) {
            return request.getTilDato();
        }
        return null;
    }

    private String defaultText(String value, String defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value;
    }

    private String normalizePrediction(String field, String prediction) {

        if (BOOLEAN_FIELDS.contains(field)) {
            return toBooleanMarker(prediction);
        }
        if (Objects.equals("UTLANDSMOTTAKER", field)) {
            return toBooleanMarker(prediction);
        }
        if (Objects.equals("VEDTAKSTATUS", field)) {
            return toVedtakStatus(prediction);
        }
        return prediction;

    }

    private String toBooleanMarker(String raw) {

        if (raw == null || raw.isBlank()) {
            return "";
        }
        if (raw.equalsIgnoreCase("J") || raw.equalsIgnoreCase("N")) {
            return raw.toUpperCase();
        }
        if (raw.equalsIgnoreCase("JA") || raw.equalsIgnoreCase("NEI")) {
            return raw.equalsIgnoreCase("JA") ? "J" : "N";
        }
        try {
            var numeric = Double.parseDouble(raw);
            return numeric >= 0.5d ? "J" : "N";
        } catch (NumberFormatException ignored) {
            return raw;
        }

    }

    private String toVedtakStatus(String raw) {

        if (raw == null || raw.isBlank()) {
            return "OPPRE";
        }
        if (Set.of("MOTAT", "OPPRE", "REGIS", "INNST", "AVSLU", "IVERK").contains(raw)) {
            return raw;
        }
        try {
            var numeric = Double.parseDouble(raw);
            if (numeric < 1.0d) {
                return "AVSLU";
            }
            if (numeric < 2.0d) {
                return "OPPRE";
            }
            if (numeric < 3.0d) {
                return "REGIS";
            }
            if (numeric < 4.0d) {
                return "INNST";
            }
            return "IVERK";
        } catch (NumberFormatException ignored) {
            return "OPPRE";
        }

    }

    private Optional<String> infer(Path modelPath, VedtakRequestDto request) {

        try {
            var session = sessionCache.computeIfAbsent(modelPath, this::createSession);
            var inputName = session.getInputNames().iterator().next();
            var featureCount = featureCountCache.computeIfAbsent(session, k -> {
                try {
                    return resolveFeatureCount(k);
                } catch (OrtException e) {
                    throw new RuntimeException(e);
                }
            });
            var features = buildFeatureVector(request, featureCount);

            try (var inputTensor = OnnxTensor.createTensor(environment, new float[][]{features});
                 var result = session.run(Map.of(inputName, inputTensor))) {
                return extractLabel(result);
            }

        } catch (Exception e) {
            return Optional.empty();
        }

    }

    private int resolveFeatureCount(OrtSession session)
            throws OrtException {

        var inputName = session.getInputNames().iterator().next();
        var input = Objects.requireNonNull(session.getInputInfo().get(inputName), "Missing ONNX input metadata");
        var tensorInfo = (TensorInfo) input.getInfo();
        var shape = tensorInfo.getShape();
        if (shape.length >= 2 && shape[1] > 0) {
            return (int) shape[1];
        }
        return 1;

    }

    private float[] buildFeatureVector(VedtakRequestDto request, int featureCount) {

        var features = new float[Math.max(1, featureCount)];
        try {
            var date = LocalDate.parse(request.getFraDato());
            features[0] = date.getYear();
            if (features.length > 1) {
                features[1] = date.getMonthValue();
            }
            if (features.length > 2) {
                features[2] = date.getDayOfMonth();
            }
        } catch (Exception ignored) {
            features[0] = random.nextInt(3000);
            if (features.length > 1) {
                features[1] = random.nextFloat();
            }
            if (features.length > 2) {
                features[2] = random.nextFloat();
            }
        }
        for (int i = 3; i < features.length; i++) {
            features[i] = random.nextFloat();
        }
        return features;

    }

    private Optional<String> extractLabel(OrtSession.Result result)
            throws OrtException {

        for (var entry : result) {
            var value = entry.getValue().getValue();
            if (value instanceof long[] longLabels && longLabels.length > 0) {
                return Optional.of(Long.toString(longLabels[0]));
            }
            if (value instanceof String[] labels && labels.length > 0) {
                return Optional.of(labels[0]);
            }
            if (value instanceof long[][] matrix && matrix.length > 0 && matrix[0].length > 0) {
                return Optional.of(Long.toString(matrix[0][0]));
            }
            if (value instanceof float[][] matrix && matrix.length > 0 && matrix[0].length > 0) {
                return Optional.of(Float.toString(matrix[0][0]));
            }
        }
        return Optional.empty();

    }

    private Map<AapModelType, Map<ModelSelector, List<DistributionModel>>> discoverDistributionModels(Path modelDirectory) {

        var discovered = new EnumMap<AapModelType, Map<ModelSelector, List<DistributionModel>>>(AapModelType.class);

        try (var stream = Files.list(modelDirectory)) {
            var files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".onnx"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .toList();

            for (var file : files) {
                var matcher = MODEL_FILE_PATTERN.matcher(file.getFileName().toString());
                if (!matcher.matches()) {
                    continue;
                }

                var modelType = AapModelType.valueOf(matcher.group(1));
                var selector = new ModelSelector(matcher.group(2), matcher.group(3));
                var columnIndex = Integer.parseInt(matcher.group(4));
                var fieldName = resolveFieldName(modelType, columnIndex);
                if (fieldName.isEmpty()) {
                    continue;
                }

                discovered
                        .computeIfAbsent(modelType, ignored -> new HashMap<>())
                        .computeIfAbsent(selector, ignored -> new ArrayList<>())
                        .add(new DistributionModel(fieldName, columnIndex, file.getFileName().toString(), file));
            }

            for (var bySelector : discovered.values()) {
                bySelector.values().forEach(distributions -> distributions.sort(Comparator.comparingInt(DistributionModel::columnIndex)));
            }
            return discovered;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to scan ONNX model directory " + modelDirectory, e);
        }

    }

    private OrtSession createSession(Path modelPath) {

        try {
            return environment.createSession(modelPath.toString(), new OrtSession.SessionOptions());
        } catch (OrtException e) {
            throw new IllegalStateException("Failed to create ONNX session for " + modelPath, e);
        }

    }

    private String resolveFieldName(AapModelType modelType, int columnIndex) {

        var columns = COLUMN_ORDER.getOrDefault(modelType, List.of());
        if (columnIndex < 0 || columnIndex >= columns.size()) {
            return "";
        }
        return columns.get(columnIndex);

    }

    private record ModelSelector(String vedtakType, String utfall) {
    }

    private record DistributionModel(String fieldName, int columnIndex, String fileName, Path modelPath) {
    }

}

