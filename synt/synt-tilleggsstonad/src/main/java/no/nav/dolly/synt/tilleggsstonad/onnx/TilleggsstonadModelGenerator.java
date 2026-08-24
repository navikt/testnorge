package no.nav.dolly.synt.tilleggsstonad.onnx;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.time.temporal.ChronoUnit;
import java.util.zip.GZIPInputStream;

@Slf4j
class TilleggsstonadModelGenerator {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter LEGACY_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ROOT);
    private static final Set<String> VEDTAKTYPER_SOM_MAA_HA_TIL_DATO = Set.of("O", "E");
    private static final Set<String> SPECIAL_NUMBER_VALUES = Set.of("nan", "infinity", "-infinity", "inf", "-inf");
    private static final List<String> VILKAAR = List.of(
            "TSTIKKELON", "TSTGSTATUS", "TSTAVSTOBL", "TSTAVSTREI", "TSTAVSTDAG", "TSTBEHTRAN", "TSTBEHOVHJ", "TSTBOUTHJE",
            "TSTHOYBOUT", "TSTMOTBOST", "TSTSAMLDOB", "TSTSAMLDOR", "TSTENSFORS", "TSTFLYTAKT", "TSTFLYTARB", "TSTVEDTAK",
            "TSTIKKDEKR", "TSTIKKDEKF", "TSTGJLEEKT", "TSTFJERDE", "TSTBARNMED", "TSTIKKYTLM", "TSTIKKYTTB", "TSTIKKYTTF",
            "TSTOGANGST", "TSTOGSTPEN", "TSTBOUTFUN", "TSTIKKARBR", "TSTLOVOPPH", "TSTFOLKETR", "TSTDAGPENG", "TSTSYKPENG",
            "TSTTILPENG", "TSTTILFAM", "TSTBOHJFRA", "TSTBOUTAKT", "TSTREGARBS", "TSTREISOPP", "TSTOFFTRAN", "TSTEGENBIL",
            "TSTDROSJE", "TSTUTGFUNK", "TSTFIREHJR", "TSTTIFAMPL", "TSTINGENMG", "TSTBARNSKO", "TSTTILNODV", "TSTBARN",
            "TSTANBKJOR", "TSTULEDIGR", "TSTUTDAKT", "TSTUTGDOKF", "TSTUTGDOKR", "TSTUTGNODF", "TSTUTGNODT", "TSTUTGNODR",
            "TSTUTGSAML", "TSTUTGDAGR", "TSTIKKARBF", "TSTULEDIGF", "TSTANNSTEF", "TSTANNSTER"
    );
    private static final Map<String, List<String>> LEGAL_AVBRUDDSKODER = Map.of(
            "MOTAT", List.of("FSOP"),
            "OPPRE", List.of("FSOP"),
            "REGIS", List.of("FSOP", "ESOP", "FFOV"),
            "INNST", List.of("EFOV", "FFAV")
    );
    private static final Map<String, List<String>> VILKAARSGRUPPER = Map.of(
            "REISE", List.of("TSTOFFTRAN", "TSTEGENBIL", "TSTDROSJE"),
            "AVSTAND", List.of("TSTAVSTDAG", "TSTBEHTRAN"),
            "BOLIG", List.of("TSTBOUTHJE", "TSTHOYBOUT"),
            "FLYTTE", List.of("TSTFLYTAKT", "TSTFLYTARB"),
            "TILSYN", List.of("TSTBARNSKO", "TSTTILNODV", "TSTBARN")
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private final Map<TilleggsstonadType, ModelMetadata> metadataCache = new ConcurrentHashMap<>();
    private final Path modelDirectory;

    TilleggsstonadModelGenerator() {
        this.modelDirectory = null;
    }

    TilleggsstonadModelGenerator(Path modelDirectory) {
        this.modelDirectory = modelDirectory;
    }

    List<Map<String, Object>> generateVedtak(TilleggsstonadType type, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        if (requests == null || requests.isEmpty()) {
            return List.of();
        }

        var metadata = metadataCache.computeIfAbsent(type, this::loadMetadata);
        var config = typeConfig(type);
        var attempts = config.requiresValidHistory() ? 20 : 1;
        List<Map<String, Object>> structuredHistory = List.of();
        for (int attempt = 1; attempt <= attempts; attempt++) {
            var rawHistory = synthesizeRawHistory(type, metadata, requests, brukInnsendtTilDato);
            structuredHistory = postprocess(type, metadata, config, rawHistory, requests, brukInnsendtTilDato);
            if (!config.requiresValidHistory() || isValidHistory(structuredHistory, config)) {
                if (config.rewritesInvalidMaalgruppe()) {
                    replaceInvalidMaalgruppe(structuredHistory, config);
                }
                return structuredHistory;
            }
        }

        throw new IllegalStateException("Failed to generate valid tilleggsstonad history for " + type);
    }

    private List<Map<String, String>> synthesizeRawHistory(TilleggsstonadType type, ModelMetadata metadata, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return switch (typeConfig(type).strategy()) {
            case GENERIC_HISTORY -> synthesizeGenericHistory(metadata, requests, brukInnsendtTilDato);
            case DIRECT_O_OR_S -> synthesizeDirectHistory(metadata, requests, brukInnsendtTilDato);
            case DIRECT_O_ONLY -> synthesizeOOnlyHistory(metadata, requests, brukInnsendtTilDato);
        };
    }

    private List<Map<String, String>> synthesizeGenericHistory(ModelMetadata metadata, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        Map<String, String> lastVedtak = null;
        var history = new ArrayList<Map<String, String>>();
        for (var request : requests) {
            var vedtakType = defaultText(request.getVedtakTypeKode(), "O");
            var utfall = defaultText(request.getUtfall(), "JA");
            switch (vedtakType) {
                case "O" -> {
                    var generated = generateRow(metadata, "O", utfall, request, brukInnsendtTilDato);
                    history.add(generated);
                    lastVedtak = new LinkedHashMap<>(generated);
                }
                case "E" -> {
                    if (lastVedtak == null) {
                        throw new IllegalArgumentException("Endringsvedtak requires a preceding original vedtak");
                    }
                    var mask = generateRow(metadata, "E", utfall, request, brukInnsendtTilDato);
                    var nyRettighet = generateRow(metadata, "O", utfall, request, brukInnsendtTilDato);
                    var updated = overwriteFields(lastVedtak, mask, nyRettighet);
                    updated.put("VEDTAKTYPE", "E");
                    history.add(updated);
                    lastVedtak = new LinkedHashMap<>(updated);
                }
                case "S" -> {
                    if (lastVedtak == null) {
                        throw new IllegalArgumentException("Stansvedtak requires a preceding original vedtak");
                    }
                    var mask = generateRow(metadata, "E", utfall, request, brukInnsendtTilDato);
                    var nyRettighet = generateRow(metadata, "O", utfall, request, brukInnsendtTilDato);
                    var updated = overwriteFields(lastVedtak, mask, nyRettighet);
                    updated.put("VEDTAKTYPE", "S");
                    updated.put("TIL_DATO", "");
                    history.add(updated);
                    lastVedtak = new LinkedHashMap<>(updated);
                }
                default -> throw new IllegalArgumentException("Unsupported vedtakTypeKode " + vedtakType);
            }
        }
        return history;
    }

    private List<Map<String, String>> synthesizeDirectHistory(ModelMetadata metadata, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        var history = new ArrayList<Map<String, String>>();
        for (var request : requests) {
            var vedtakType = defaultText(request.getVedtakTypeKode(), "O");
            if (!Set.of("O", "S").contains(vedtakType)) {
                throw new IllegalArgumentException("Unsupported vedtakTypeKode " + vedtakType);
            }
            history.add(generateRow(metadata, vedtakType, defaultText(request.getUtfall(), "JA"), request, brukInnsendtTilDato));
        }
        return history;
    }

    private List<Map<String, String>> synthesizeOOnlyHistory(ModelMetadata metadata, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        var history = new ArrayList<Map<String, String>>();
        for (var request : requests) {
            var vedtakType = defaultText(request.getVedtakTypeKode(), "O");
            if (!Objects.equals("O", vedtakType)) {
                throw new IllegalArgumentException("Unsupported vedtakTypeKode " + vedtakType);
            }
            history.add(generateRow(metadata, "O", defaultText(request.getUtfall(), "JA"), request, brukInnsendtTilDato));
        }
        return history;
    }

    private Map<String, String> overwriteFields(Map<String, String> previousVedtak, Map<String, String> mask, Map<String, String> nyRettighet) {
        var updated = new LinkedHashMap<>(previousVedtak);
        for (var entry : mask.entrySet()) {
            if (Objects.equals("1", entry.getValue())) {
                updated.put(entry.getKey(), nyRettighet.get(entry.getKey()));
            }
        }
        return updated;
    }

    private Map<String, String> generateRow(ModelMetadata metadata, String selector, String utfall, VedtakRequestDto request, boolean brukInnsendtTilDato) {
        var selectorModels = metadata.selectors().get(selector);
        if (selectorModels == null) {
            throw new IllegalStateException("Missing selector " + selector + " in legacy metadata");
        }
        var model = selectorModels.get(utfall);
        if (model == null) {
            throw new IllegalStateException("Missing outcome " + utfall + " for selector " + selector + " in legacy metadata");
        }

        var rawValues = new LinkedHashMap<String, String>();
        var encodedValues = new HashMap<Integer, Double>();
        for (int index = 0; index < model.columnNames().size(); index++) {
            var initial = initialValue(model, metadata, index, request, brukInnsendtTilDato, selector, utfall);
            if (hasValue(initial)) {
                rawValues.put(model.columnNames().get(index), initial);
            }
            var encoded = encodeValue(model, index, initial);
            if (encoded != null) {
                encodedValues.put(index, encoded);
            }
        }

        for (var index : model.visitSequence()) {
            var distribution = model.distributions().get(index);
            if (distribution == null) {
                continue;
            }
            var encodedPrediction = switch (distribution.kind()) {
                case SAMPLE -> sampleWeighted(distribution.sampleWeights());
                case CART -> evaluateCart(distribution, encodedValues);
            };
            encodedValues.put(index, (double) encodedPrediction);
            rawValues.put(model.columnNames().get(index), decodeValue(model, index, encodedPrediction));
        }

        rawValues.putIfAbsent("VEDTAKTYPE", selector);
        rawValues.putIfAbsent("UTFALL", utfall);
        rawValues.putIfAbsent("FRA_DATO", defaultText(request.getFraDato(), ""));
        return rawValues;
    }

    private String initialValue(SelectorModel model, ModelMetadata metadata, int index, VedtakRequestDto request, boolean brukInnsendtTilDato, String selector, String utfall) {
        var columnName = model.columnNames().get(index);
        if (Objects.equals("VEDTAKTYPE", columnName)) {
            return selector;
        }
        if (Objects.equals("UTFALL", columnName)) {
            return utfall;
        }
        if (Objects.equals("FRA_DATO", columnName)) {
            return defaultText(request.getFraDato(), "");
        }
        if (metadata.deltaColumns().contains(columnName)) {
            return deltaValue(columnName, request, brukInnsendtTilDato);
        }
        if (Objects.equals("TIL_DATO", columnName) && brukInnsendtTilDato) {
            return defaultText(request.getTilDato(), "");
        }
        if (Objects.equals("DATO_MOTTATT", columnName) && !model.distributions().containsKey(index)) {
            return defaultText(request.getVedtakDato(), request.getFraDato());
        }
        return "";
    }

    private String deltaValue(String columnName, VedtakRequestDto request, boolean brukInnsendtTilDato) {
        var fraDato = parseDate(request.getFraDato());
        if (fraDato == null) {
            return "";
        }

        String dateValue = switch (columnName) {
            case "DATO_MOTTATT" -> defaultText(request.getVedtakDato(), request.getFraDato());
            case "TIL_DATO" -> brukInnsendtTilDato ? defaultText(request.getTilDato(), "") : "";
            default -> "";
        };
        var target = parseDate(dateValue);
        if (target == null) {
            return "";
        }
        return Long.toString(ChronoUnit.DAYS.between(fraDato, target));
    }

    private Double encodeValue(SelectorModel model, int index, String value) {
        if (!hasValue(value)) {
            return null;
        }
        var encoder = model.labelEncoders().get(index);
        if (encoder == null || encoder.isEmpty()) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return 0d;
            }
        }
        for (int i = 0; i < encoder.size(); i++) {
            if (Objects.equals(encoder.get(i), value)) {
                return (double) i;
            }
        }
        try {
            var numericValue = Double.parseDouble(value);
            var bestIndex = 0;
            var bestDistance = Double.MAX_VALUE;
            for (int i = 0; i < encoder.size(); i++) {
                try {
                    var distance = Math.abs(Double.parseDouble(encoder.get(i)) - numericValue);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        bestIndex = i;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
            return (double) bestIndex;
        } catch (NumberFormatException ignored) {
            return 0d;
        }
    }

    private String decodeValue(SelectorModel model, int index, int encodedPrediction) {
        var encoder = model.labelEncoders().get(index);
        if (encoder == null || encoder.isEmpty()) {
            return Integer.toString(encodedPrediction);
        }
        if (encodedPrediction < 0 || encodedPrediction >= encoder.size()) {
            return encoder.getFirst();
        }
        return encoder.get(encodedPrediction);
    }

    private int evaluateCart(Distribution distribution, Map<Integer, Double> encodedValues) {
        var nodeIndex = 0;
        while (true) {
            var node = distribution.tree().get(nodeIndex);
            if (node.feature() < 0 || node.left() < 0 || node.right() < 0) {
                break;
            }
            var featureValue = encodedValues.getOrDefault(node.feature(), 0d);
            nodeIndex = featureValue <= node.threshold() ? node.left() : node.right();
        }
        var leafProbabilities = distribution.leafProbabilities().get(nodeIndex);
        if (leafProbabilities != null && !leafProbabilities.isEmpty()) {
            return sampleWeighted(leafProbabilities);
        }
        if (distribution.defaultValue() != null) {
            return distribution.defaultValue();
        }
        return argMax(distribution.tree().get(nodeIndex).values());
    }

    private int sampleWeighted(Map<Integer, Double> weights) {
        if (weights == null || weights.isEmpty()) {
            return 0;
        }
        var total = weights.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total <= 0d) {
            return weights.keySet().iterator().next();
        }
        var randomValue = random.nextDouble(total);
        var cumulative = 0d;
        for (var entry : weights.entrySet()) {
            cumulative += entry.getValue();
            if (randomValue <= cumulative) {
                return entry.getKey();
            }
        }
        return weights.keySet().iterator().next();
    }

    private int argMax(List<Double> values) {
        var bestIndex = 0;
        var bestValue = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) > bestValue) {
                bestValue = values.get(i);
                bestIndex = i;
            }
        }
        return bestIndex;
    }

    private List<Map<String, Object>> postprocess(TilleggsstonadType type, ModelMetadata metadata, TypeConfiguration config, List<Map<String, String>> rawHistory, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        var structuredHistory = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < rawHistory.size(); i++) {
            var rawVedtak = new LinkedHashMap<>(rawHistory.get(i));
            convertRelativeDates(rawVedtak, metadata.root(), metadata.deltaColumns(), config.differentTimeFormats(), requests.get(i), brukInnsendtTilDato);
            applyKde(rawVedtak, metadata.kdeKeys());
            normalizeSpecialNumberValues(rawVedtak);
            structuredHistory.add(toStructuredVedtak(type, config, rawVedtak));
        }
        return structuredHistory;
    }

    private void convertRelativeDates(Map<String, String> vedtak, String root, List<String> deltaColumns, Set<String> differentTimeFormats, VedtakRequestDto request, boolean brukInnsendtTilDato) {
        var startDate = parseDate(request.getFraDato());
        if (startDate == null) {
            return;
        }
        vedtak.put(root, ISO_DATE.format(startDate));

        var effectiveColumns = new ArrayList<>(deltaColumns);
        var endDate = parseDate(request.getTilDato());
        if (brukInnsendtTilDato && endDate != null) {
            effectiveColumns.remove("TIL_DATO");
            var wantedDelta = ChronoUnit.DAYS.between(startDate, endDate);
            var actualDelta = parseInteger(vedtak.get("TIL_DATO"));
            var scaleFactor = (actualDelta != null && actualDelta != 0) ? (double) wantedDelta / actualDelta : 1d;
            for (var column : effectiveColumns) {
                var value = parseInteger(vedtak.get(column));
                if (value != null) {
                    vedtak.put(column, Integer.toString((int) (value * scaleFactor)));
                }
            }
            vedtak.put("TIL_DATO", ISO_DATE.format(endDate));
        }

        for (var column : effectiveColumns) {
            var value = parseInteger(vedtak.get(column));
            if (value == null) {
                vedtak.put(column, "");
                continue;
            }
            var resolvedDate = startDate.plusDays(value);
            vedtak.put(column, differentTimeFormats.contains(column) ? LEGACY_DATE.format(resolvedDate) : ISO_DATE.format(resolvedDate));
        }

        var vedtakType = defaultText(vedtak.get("VEDTAKTYPE"), "O");
        if (!brukInnsendtTilDato && VEDTAKTYPER_SOM_MAA_HA_TIL_DATO.contains(vedtakType) && !hasValue(vedtak.get("TIL_DATO"))) {
            vedtak.put("TIL_DATO", defaultText(request.getTilDato(), ""));
        }
    }

    private void applyKde(Map<String, String> vedtak, Set<String> kdeKeys) {
        for (var key : kdeKeys) {
            var numericValue = parseDouble(vedtak.get(key));
            if (numericValue != null) {
                vedtak.put(key, formatDecimal(Math.max(numericValue, 0d)));
            } else if (hasValue(vedtak.get(key))) {
                vedtak.put(key, "0");
            }
        }
    }

    private Map<String, Object> toStructuredVedtak(TilleggsstonadType type, TypeConfiguration config, Map<String, String> source) {
        var vedtak = new LinkedHashMap<String, Object>(source);
        addAvbruddskode(vedtak);
        kodeOverordnetVerdi(vedtak, type.getResultField(), config.values());
        applyVilkaar(vedtak);
        vedtaksPeriode(vedtak);
        vedtak.put("RETTIGHET_KODE", config.rettighetKode());
        wrapNestedList(vedtak, type.getResultField(), config.wrapperElementName());
        return vedtak;
    }

    private void addAvbruddskode(Map<String, Object> vedtak) {
        var status = (String) vedtak.remove("VEDTAKSTATUS");
        if (LEGAL_AVBRUDDSKODER.containsKey(status)) {
            var avbruddskoder = LEGAL_AVBRUDDSKODER.get(status);
            vedtak.put("AVBRUDDSKODE", avbruddskoder.get(random.nextInt(avbruddskoder.size())));
        }
    }

    private void kodeOverordnetVerdi(Map<String, Object> vedtak, String groupName, List<String> values) {
        var groupedValues = new ArrayList<Map<String, Object>>();
        for (var code : values) {
            var separator = code.indexOf('_');
            String overordnet = null;
            String fieldName = code;
            if (separator >= 0) {
                overordnet = code.substring(0, separator);
                fieldName = code.substring(separator + 1);
                if (!containsKode(groupedValues, overordnet)) {
                    var overordnetEntry = new LinkedHashMap<String, Object>();
                    overordnetEntry.put("KODE", overordnet);
                    overordnetEntry.put("OVERORDNET", null);
                    overordnetEntry.put("VERDI", null);
                    groupedValues.add(overordnetEntry);
                    vedtak.remove(overordnet);
                }
            }
            var valueEntry = new LinkedHashMap<String, Object>();
            valueEntry.put("KODE", fieldName);
            valueEntry.put("OVERORDNET", overordnet);
            valueEntry.put("VERDI", defaultText((String) vedtak.remove(code), ""));
            groupedValues.add(valueEntry);
        }
        vedtak.put(groupName, groupedValues);
    }

    private void applyVilkaar(Map<String, Object> vedtak) {
        var vilkaarValues = new ArrayList<Map<String, String>>();
        var oppfyltGruppe = new HashMap<String, Boolean>();
        VILKAARSGRUPPER.keySet().forEach(group -> oppfyltGruppe.put(group, false));
        var utfall = (String) vedtak.get("UTFALL");
        for (var code : VILKAAR) {
            if (!vedtak.containsKey(code)) {
                continue;
            }
            var status = defaultText((String) vedtak.remove(code), "");
            if (Objects.equals("JA", utfall)) {
                for (var entry : VILKAARSGRUPPER.entrySet()) {
                    if (entry.getValue().contains(code)) {
                        if (Boolean.TRUE.equals(oppfyltGruppe.get(entry.getKey()))) {
                            status = "N";
                        }
                        if (Objects.equals("J", status)) {
                            oppfyltGruppe.put(entry.getKey(), true);
                        }
                        break;
                    }
                }
            }
            vilkaarValues.add(new LinkedHashMap<>(Map.of("KODE", code, "STATUS", status)));
        }
        vedtak.put("VILKAAR", vilkaarValues);
    }

    private void vedtaksPeriode(Map<String, Object> vedtak) {
        vedtak.put("VEDTAKSPERIODE", new LinkedHashMap<>(Map.of(
                "FOM", defaultText((String) vedtak.remove("FRA_DATO"), ""),
                "TOM", defaultText((String) vedtak.remove("TIL_DATO"), "")
        )));
    }

    private void wrapNestedList(Map<String, Object> vedtak, String fieldName, String wrapperElementName) {
        if (!hasValue(wrapperElementName) || !(vedtak.get(fieldName) instanceof List<?> values)) {
            return;
        }
        vedtak.put(fieldName, List.of(Map.of(wrapperElementName, values)));
    }

    private boolean isValidHistory(List<Map<String, Object>> history, TypeConfiguration config) {
        if (history.isEmpty()) {
            return false;
        }
        for (var vedtak : history) {
            var maalgruppe = (String) vedtak.get("MAALGRUPPEKODE");
            if (config.invalidWithoutDate().contains(maalgruppe)) {
                return false;
            }
            if (config.invalidWithDate().contains(maalgruppe) && hasValue(config.invalidDate())) {
                var periode = castMap(vedtak.get("VEDTAKSPERIODE"));
                var fom = periode == null ? null : (String) periode.get("FOM");
                var fomDate = parseDate(fom);
                var invalidDate = parseDate(config.invalidDate());
                if (fomDate != null && invalidDate != null && fomDate.isAfter(invalidDate)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void replaceInvalidMaalgruppe(List<Map<String, Object>> history, TypeConfiguration config) {
        if (config.replacementMaalgrupper().isEmpty()) {
            return;
        }
        var replacement = config.replacementMaalgrupper().get(random.nextInt(config.replacementMaalgrupper().size()));
        for (var vedtak : history) {
            var maalgruppe = (String) vedtak.get("MAALGRUPPEKODE");
            if (config.invalidWithoutDate().contains(maalgruppe)) {
                vedtak.put("MAALGRUPPEKODE", replacement);
                continue;
            }
            if (config.invalidWithDate().contains(maalgruppe) && hasValue(config.invalidDate())) {
                var periode = castMap(vedtak.get("VEDTAKSPERIODE"));
                var fom = periode == null ? null : (String) periode.get("FOM");
                var fomDate = parseDate(fom);
                var invalidDate = parseDate(config.invalidDate());
                if (fomDate != null && invalidDate != null && fomDate.isAfter(invalidDate)) {
                    vedtak.put("MAALGRUPPEKODE", replacement);
                }
            }
        }
    }

    private TypeConfiguration typeConfig(TilleggsstonadType type) {
        return switch (type) {
            case BOUTGIFT -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "AKDAT", "AKDAT_SAFRA", "AKDAT_SATIL", "BOFAS", "BOSAM", "BOUAK", "BOUTG", "UTGHJ", "BFUTG", "BEGRU", "MBBOS", "BOMND", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL", "AKDAT_SAFRA", "AKDAT_SATIL"),
                    "TSOBOUTG",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case BOUTGIFTER_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "AKDAT", "AKDAT_SAFRA", "AKDAT_SATIL", "BOFAS", "BOSAM", "BOUAK", "BOUTG", "UTGHJ", "BFUTG", "BEGRU", "MBBOS", "BOMND", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL", "AKDAT_SAFRA", "AKDAT_SATIL"),
                    "TSRBOUTG",
                    Set.of("MOTTILTPEN"),
                    "2019-12-31",
                    Set.of("GJENEKARBS", "ENSFORARBS", "MOTDAGPEN", "ARBSOKERE"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "ENSFORUTD", "TIDLFAMPL"),
                    null
            );
            case DAGLIG_REISE -> new TypeConfiguration(
                    SynthesisStrategy.DIRECT_O_OR_S,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ADAKT", "AVBOS", "BEGRU", "OFFTR", "BERRT", "BEGRN", "BILBE", "UTGBP", "UTGPA", "DAUTG", "FREKV", "BIBBN", "UTDRO", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL"),
                    "TSODAGREIS",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case DAGLIG_REISE_ARBEIDSSOKER -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ADAKT", "AVBOS", "BEGRU", "OFFTR", "BERRT", "BEGRN", "BILBE", "UTGBP", "UTGPA", "DAUTG", "FREKV", "BIBBN", "UTDRO", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL"),
                    "TSRDAGREIS",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case LAEREMIDLER -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "NISKO", "PRUAK", "FAUTG", "BEGRU", "UDEKK", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL", "AKDAT_SAFRA", "AKDAT_SATIL"),
                    "TSOLMIDLER",
                    Set.of(),
                    "",
                    Set.of("ENSFORARBS", "GJENEKARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case LAEREMIDLER_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "NISKO", "PRUAK", "FAUTG", "BEGRU", "UDEKK", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL"),
                    "TSRLMIDLER",
                    Set.of("MOTTILTPEN"),
                    "2019-12-31",
                    Set.of("ENSFORARBS", "GJENEKARBS", "MOTDAGPEN", "ARBSOKERE"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "ENSFORUTD", "TIDLFAMPL"),
                    null
            );
            case FLYTTING -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("FLDAT", "FLAKT", "FLSTI", "NSTIT", "TIADR", "KOFLY", "ANTKM", "FLAN1", "FLAN2", "BELO1", "BELO2", "LAND"),
                    Set.of(),
                    "TSOFLYTT",
                    Set.of("ENSFORARBS", "ENSFORUTD"),
                    "2017-12-31",
                    Set.of("ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case FLYTTING_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("FLDAT", "FLAKT", "FLSTI", "NSTIT", "TIADR", "KOFLY", "ANTKM", "FLAN1", "FLAN2", "BELO1", "BELO2", "LAND"),
                    Set.of(),
                    "TSRFLYTT",
                    Set.of("MOTDAGPEN", "MOTTILTPEN"),
                    "2019-12-31",
                    Set.of("MOTDAGPEN"),
                    List.of(),
                    null
            );
            case REISE_AKTIVITET_OG_HJEMREISER -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ADAKT", "KMBOS", "AKTVA", "BAUFE", "BAUFM", "BEGHJ", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND"),
                    Set.of("KRPER_KRFRA", "KRPER_KRTIL"),
                    "TSOREISAKT",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case REISE_AKTIVITET_OG_HJEMREISER_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ADAKT", "KMBOS", "AKTVA", "BAUFE", "BAUFM", "BEGHJ", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND"),
                    Set.of(),
                    "TSRREISAKT",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case REISE_TIL_OBLIGATORISK_SAMLING -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "DAAKT", "DAAKT_SAFRA", "DAAKT_SATIL", "ADAKT", "AVBOS", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND"),
                    Set.of(),
                    "TSOREISOBL",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case REISE_TIL_OBLIGATORISK_SAMLING_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "DAAKT", "DAAKT_SAFRA", "DAAKT_SATIL", "ADAKT", "AVBOS", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND"),
                    Set.of(),
                    "TSRREISOBL",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case REISESTONAD_TIL_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.DIRECT_O_ONLY,
                    List.of("KRPER", "BEGRU", "RETIL", "KMBOS", "UDEKK", "FO8UK", "TB8UK", "OFFTR", "BELPR", "BEGRN", "BILBE", "UTGBP", "BIBBN", "UTDRO", "LAND"),
                    Set.of(),
                    "TSOREISARB",
                    Set.of(),
                    "",
                    Set.of("ENSFORUTD", "MOTDAGPEN"),
                    List.of(),
                    null
            );
            case TILSYN_BARN -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "BAUTG", "TILAV", "BA4SK", "LAND"),
                    Set.of(),
                    "TSOTILBARN",
                    Set.of("ENSFORARBS", "ENSFORUTD"),
                    "2017-12-31",
                    Set.of("ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    "BARN"
            );
            case TILSYN_BARN_ARBEIDSSOKER -> new TypeConfiguration(
                    SynthesisStrategy.GENERIC_HISTORY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "BAUTG", "TILAV", "BA4SK", "LAND"),
                    Set.of(),
                    "TSRTILBARN",
                    Set.of("MOTTILTPEN"),
                    "2019-12-31",
                    Set.of("ARBSOKERE", "MOTDAGPEN"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "GJENEKARBS", "ENSFORUTD", "ENSFORARBS", "TIDLFAMPL"),
                    "BARN"
            );
            case TILSYN_FAMILIEMEDLEMMER -> new TypeConfiguration(
                    SynthesisStrategy.DIRECT_O_ONLY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ANFNR", "TILAV", "FANAV", "BEGRU", "LAND"),
                    Set.of(),
                    "TSOTILFAM",
                    Set.of(),
                    "",
                    Set.of("GJENEKARBS", "ENSFORARBS", "ARBSOKERE", "MOTTILTPEN", "MOTDAGPEN"),
                    List.of(),
                    "FAMILIEMEDLEM"
            );
            case TILSYN_FAMILIEMEDLEMMER_ARBEIDSSOKERE -> new TypeConfiguration(
                    SynthesisStrategy.DIRECT_O_ONLY,
                    List.of("KRPER", "KRPER_KRFRA", "KRPER_KRTIL", "ANFNR", "TILAV", "FANAV", "BEGRU", "LAND"),
                    Set.of(),
                    "TSRTILFAM",
                    Set.of("MOTTILTPEN"),
                    "2019-12-31",
                    Set.of("ARBSOKERE", "MOTDAGPEN"),
                    List.of("NEDSARBEVN", "GJENEKUTD", "GJENEKARBS", "ENSFORUTD", "ENSFORARBS", "TIDLFAMPL"),
                    "FAMILIEMEDLEM"
            );
        };
    }

    private ModelMetadata loadMetadata(TilleggsstonadType type) {
        var modelFileName = type.getModelName() + ".json.gz";
        try (var stream = openModelStream(modelFileName)) {
            var root = objectMapper.readTree(new GZIPInputStream(stream));
            var selectors = new HashMap<String, Map<String, SelectorModel>>();
            root.path("sel").properties().forEach(selectorEntry -> {
                var byOutcome = new HashMap<String, SelectorModel>();
                selectorEntry.getValue().properties().forEach(outcomeEntry -> byOutcome.put(outcomeEntry.getKey(), parseSelectorModel(outcomeEntry.getValue())));
                selectors.put(selectorEntry.getKey(), byOutcome);
            });
            return new ModelMetadata(
                    root.path("r").asText(),
                    readTextList(root.path("dc")),
                    selectors,
                    Set.copyOf(readTextList(root.path("kde")))
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load legacy model metadata for " + type, e);
        }
    }

    private InputStream openModelStream(String modelFileName)
            throws IOException {

        if (modelDirectory != null) {
            var modelPath = modelDirectory.resolve(modelFileName);
            if (!Files.exists(modelPath)) {
                throw new IllegalStateException("Missing legacy model metadata file " + modelPath);
            }
            return Files.newInputStream(modelPath);
        }
        var resourcePath = "/models/" + modelFileName;
        var stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            throw new IllegalStateException("Missing legacy model metadata resource " + resourcePath);
        }
        return stream;

    }

    private SelectorModel parseSelectorModel(JsonNode node) {
        var distributions = new HashMap<Integer, Distribution>();
        node.path("d").properties().forEach(entry -> distributions.put(Integer.parseInt(entry.getKey()), parseDistribution(entry.getValue())));
        return new SelectorModel(
                readTextList(node.path("n")),
                readIntList(node.path("v")),
                readTextList(node.path("s")),
                readBooleanList(node.path("e")),
                readLabelEncoders(node.path("l")),
                distributions
        );
    }

    private Distribution parseDistribution(JsonNode node) {
        var kind = "s".equals(node.path("k").asText()) ? DistributionKind.SAMPLE : DistributionKind.CART;
        var weights = new LinkedHashMap<Integer, Double>();
        node.path("w").properties().forEach(entry -> weights.put(Integer.parseInt(entry.getKey()), entry.getValue().asDouble()));
        var tree = new ArrayList<TreeNode>();
        for (var item : node.path("t")) {
            tree.add(new TreeNode(item.path("l").asInt(), item.path("r").asInt(), item.path("f").asInt(), item.path("t").asDouble(), readDoubleList(item.path("v"))));
        }
        var leafProbabilities = new HashMap<Integer, Map<Integer, Double>>();
        node.path("p").properties().forEach(entry -> {
            var probabilities = new LinkedHashMap<Integer, Double>();
            entry.getValue().properties().forEach(probability -> probabilities.put(Integer.parseInt(probability.getKey()), probability.getValue().asDouble()));
            leafProbabilities.put(Integer.parseInt(entry.getKey()), probabilities);
        });
        return new Distribution(kind, weights, tree, leafProbabilities, node.path("x").isMissingNode() || node.path("x").isNull() ? null : node.path("x").asInt());
    }

    private List<String> readTextList(JsonNode node) {
        var values = new ArrayList<String>();
        if (node == null || node.isMissingNode() || node.isNull()) {
            return values;
        }
        node.forEach(item -> values.add(item.isNull() ? null : item.asText()));
        return values;
    }

    private List<Integer> readIntList(JsonNode node) {
        var values = new ArrayList<Integer>();
        node.forEach(item -> values.add(item.asInt()));
        return values;
    }

    private List<Boolean> readBooleanList(JsonNode node) {
        var values = new ArrayList<Boolean>();
        node.forEach(item -> values.add(item.asBoolean()));
        return values;
    }

    private List<Double> readDoubleList(JsonNode node) {
        var values = new ArrayList<Double>();
        node.forEach(item -> values.add(item.asDouble()));
        return values;
    }

    private List<List<String>> readLabelEncoders(JsonNode node) {
        var encoders = new ArrayList<List<String>>();
        node.forEach(item -> encoders.add(item.isNull() ? null : readTextList(item)));
        return encoders;
    }

    private LocalDate parseDate(String value) {
        if (!hasValue(value)) {
            return null;
        }
        try {
            return LocalDate.parse(value, ISO_DATE);
        } catch (Exception ignored) {
            try {
                return LocalDate.parse(value, LEGACY_DATE);
            } catch (Exception ignoredAgain) {
                return null;
            }
        }
    }

    private Integer parseInteger(String value) {
        if (!hasValue(value)) {
            return null;
        }
        try {
            return (int) Math.round(Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String value) {
        if (!hasValue(value)) {
            return null;
        }
        try {
            var parsed = Double.parseDouble(value);
            return Double.isFinite(parsed) ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String defaultText(String value, String defaultValue) {
        return hasValue(value) ? value : defaultValue;
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank() && !isSpecialNumberValue(value);
    }

    private void normalizeSpecialNumberValues(Map<String, String> vedtak) {
        vedtak.replaceAll((ignored, value) -> isSpecialNumberValue(value) ? "" : value);
    }

    private boolean isSpecialNumberValue(String value) {
        if (value == null) {
            return false;
        }
        var normalized = value.trim().toLowerCase(Locale.ROOT);
        return SPECIAL_NUMBER_VALUES.contains(normalized);
    }

    private String formatDecimal(double value) {
        if (!Double.isFinite(value)) {
            return "0";
        }
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.DOWN).stripTrailingZeros().toPlainString();
    }

    private boolean containsKode(List<Map<String, Object>> values, String kode) {
        for (var value : values) {
            if (Objects.equals(value.get("KODE"), kode)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> castMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    private record ModelMetadata(
            String root,
            List<String> deltaColumns,
            Map<String, Map<String, SelectorModel>> selectors,
            Set<String> kdeKeys
    ) {
    }

    private record SelectorModel(
            List<String> columnNames,
            List<Integer> visitSequence,
            List<String> synthMethod,
            List<Boolean> excluded,
            List<List<String>> labelEncoders,
            Map<Integer, Distribution> distributions
    ) {
    }

    private record Distribution(
            DistributionKind kind,
            Map<Integer, Double> sampleWeights,
            List<TreeNode> tree,
            Map<Integer, Map<Integer, Double>> leafProbabilities,
            Integer defaultValue
    ) {
    }

    private record TreeNode(
            int left,
            int right,
            int feature,
            double threshold,
            List<Double> values
    ) {
    }

    private record TypeConfiguration(
            SynthesisStrategy strategy,
            List<String> values,
            Set<String> differentTimeFormats,
            String rettighetKode,
            Set<String> invalidWithDate,
            String invalidDate,
            Set<String> invalidWithoutDate,
            List<String> replacementMaalgrupper,
            String wrapperElementName
    ) {
        boolean rewritesInvalidMaalgruppe() {
            return !replacementMaalgrupper.isEmpty();
        }

        boolean requiresValidHistory() {
            return !rewritesInvalidMaalgruppe() && (!invalidWithDate.isEmpty() || !invalidWithoutDate.isEmpty());
        }
    }

    private enum DistributionKind {
        SAMPLE,
        CART
    }

    private enum SynthesisStrategy {
        GENERIC_HISTORY,
        DIRECT_O_OR_S,
        DIRECT_O_ONLY
    }

}
