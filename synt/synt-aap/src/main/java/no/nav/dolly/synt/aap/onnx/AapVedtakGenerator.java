package no.nav.dolly.synt.aap.onnx;

import no.nav.dolly.synt.aap.dto.VedtakRequestDto;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class AapVedtakGenerator {

    private final Map<AapModelType, Path> modelByType;

    AapVedtakGenerator(Path modelDirectory) {
        modelByType = discoverOneModelPerType(modelDirectory);
    }

    List<Map<String, String>> generateVedtak(AapModelType modelType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {

        var modelName = Optional
                .ofNullable(modelByType.get(modelType))
                .map(path -> path.getFileName().toString())
                .orElseThrow(() -> new IllegalArgumentException("No ONNX model found for type " + modelType));

        return requests
                .stream()
                .map(request -> toVedtak(modelType, request, brukInnsendtTilDato, modelName))
                .toList();

    }

    private Map<String, String> toVedtak(AapModelType modelType, VedtakRequestDto request, boolean brukInnsendtTilDato, String modelName) {
        return switch (modelType) {
            case AAP -> aapVedtak(request, brukInnsendtTilDato, modelName);
            case AAP_115 -> vedtak115(request, brukInnsendtTilDato, modelName);
            case AATFOR, AAUNGUFOR, FRI_MK -> enkelVedtak(request, brukInnsendtTilDato, modelName);
        };
    }

    private Map<String, String> aapVedtak(VedtakRequestDto request, boolean brukInnsendtTilDato, String modelName) {

        var vedtak = new LinkedHashMap<String, String>();
        vedtak.put("FRA_DATO", request.getFraDato());
        vedtak.put("TIL_DATO", resolvedTilDato(request, brukInnsendtTilDato));
        vedtak.put("UTFALL", defaultText(request.getUtfall(), "JA"));
        vedtak.put("VEDTAKTYPE", defaultText(request.getVedtakTypeKode(), "O"));
        vedtak.put("DATO_MOTTATT", defaultText(request.getVedtakDato(), request.getFraDato()));
        vedtak.put("VEDTAK_DATO", defaultText(request.getVedtakDato(), request.getFraDato()));
        vedtak.put("AKTFASEKODE", "UA");
        vedtak.put("_MODEL", modelName);
        return vedtak;

    }

    private Map<String, String> vedtak115(VedtakRequestDto request, boolean brukInnsendtTilDato, String modelName) {

        var vedtak = new LinkedHashMap<String, String>();
        vedtak.put("FRA_DATO", request.getFraDato());
        vedtak.put("TIL_DATO", resolvedTilDato(request, brukInnsendtTilDato));
        vedtak.put("UTFALL", defaultText(request.getUtfall(), "JA"));
        vedtak.put("VEDTAKTYPE", defaultText(request.getVedtakTypeKode(), "O"));
        vedtak.put("DATO_MOTTATT", defaultText(request.getVedtakDato(), request.getFraDato()));
        vedtak.put("_MODEL", modelName);
        return vedtak;

    }

    private Map<String, String> enkelVedtak(VedtakRequestDto request, boolean brukInnsendtTilDato, String modelName) {

        var vedtak = new LinkedHashMap<String, String>();
        vedtak.put("FRA_DATO", request.getFraDato());
        vedtak.put("TIL_DATO", resolvedTilDato(request, brukInnsendtTilDato));
        vedtak.put("UTFALL", defaultText(request.getUtfall(), "JA"));
        vedtak.put("VEDTAKTYPE", defaultText(request.getVedtakTypeKode(), "O"));
        vedtak.put("DATO_MOTTATT", defaultText(request.getVedtakDato(), request.getFraDato()));
        vedtak.put("_MODEL", modelName);
        return vedtak;

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

    private Map<AapModelType, Path> discoverOneModelPerType(Path modelDirectory) {

        try (var stream = Files.list(modelDirectory)) {

            var files = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".onnx"))
                    .toList();

            var selected = new EnumMap<AapModelType, Path>(AapModelType.class);
            for (var type : AapModelType.values()) {
                files
                        .stream()
                        .filter(path -> path.getFileName().toString().startsWith(type.name()))
                        .findFirst()
                        .ifPresent(path -> selected.put(type, path));
            }
            return selected;

        } catch (Exception e) {
            throw new IllegalStateException("Failed to scan ONNX model directory " + modelDirectory, e);
        }

    }

}

