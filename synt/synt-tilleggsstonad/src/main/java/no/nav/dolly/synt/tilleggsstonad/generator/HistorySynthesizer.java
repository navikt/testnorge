package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import static java.util.Objects.isNull;

@UtilityClass
class HistorySynthesizer {

    static final String VEDTAKTYPE = "VEDTAKTYPE";
    static final String TIL_DATO = "TIL_DATO";
    static final String ORDINAER = "O";
    static final String ENDRING = "E";
    static final String STANS = "S";
    private static final String OVERWRITE_MARKER = "1";

    static List<Map<String, Object>> synthesize(TilleggsstonadModel model, TilleggsstonadType.HistorySynthesis variant,
                                                List<VedtakRequestDto> requests, RandomGenerator random) {

        var historikk = switch (variant) {
            case STANDARD, STANDARD_WITH_KERNEL_DENSITY -> synthesizeStandard(model, requests, random);
            case ONLY_O -> synthesizeDirect(model, requests, random, List.of(ORDINAER));
            case ONLY_O_AND_S -> synthesizeDirect(model, requests, random, List.of(ORDINAER, STANS));
        };
        if (variant == TilleggsstonadType.HistorySynthesis.STANDARD_WITH_KERNEL_DENSITY) {
            applyKernelDensities(model, historikk, random);
        }
        return historikk;

    }

    private static List<Map<String, Object>> synthesizeStandard(TilleggsstonadModel model, List<VedtakRequestDto> requests,
                                                                RandomGenerator random) {

        var batches = new Batches(model, random);
        var historikk = new ArrayList<Map<String, Object>>(requests.size());
        Map<String, Object> forrigeVedtak = null;
        for (var request : requests) {
            var vedtaktype = request.getVedtakTypeKode();
            var utfall = request.getUtfall();
            if (ORDINAER.equals(vedtaktype)) {
                var vedtak = batches.next(ORDINAER, utfall);
                historikk.add(vedtak);
                forrigeVedtak = vedtak;
            } else if (ENDRING.equals(vedtaktype) || STANS.equals(vedtaktype)) {
                var endringsvedtak = batches.next(ENDRING, utfall);
                var nyRettighet = batches.next(ORDINAER, utfall);
                if (isNull(forrigeVedtak)) {
                    throw new IllegalArgumentException("Vedtak av type %s kan ikke komme før et vedtak av type O".formatted(vedtaktype));
                }
                var vedtak = new LinkedHashMap<>(forrigeVedtak);
                overwriteMarkedColumns(vedtak, endringsvedtak, nyRettighet);
                vedtak.put(VEDTAKTYPE, vedtaktype);
                if (STANS.equals(vedtaktype)) {
                    vedtak.put(TIL_DATO, "");
                }
                historikk.add(vedtak);
                forrigeVedtak = vedtak;
            } else {
                throw unsupportedVedtaktype(vedtaktype);
            }
        }
        return historikk;

    }

    private static List<Map<String, Object>> synthesizeDirect(TilleggsstonadModel model, List<VedtakRequestDto> requests,
                                                              RandomGenerator random, List<String> vedtaktyper) {

        var batches = new Batches(model, random);
        var historikk = new ArrayList<Map<String, Object>>(requests.size());
        for (var request : requests) {
            var vedtaktype = request.getVedtakTypeKode();
            if (isNull(vedtaktype) || !vedtaktyper.contains(vedtaktype)) {
                throw unsupportedVedtaktype(vedtaktype);
            }
            historikk.add(batches.next(vedtaktype, request.getUtfall()));
        }
        return historikk;

    }

    private static void overwriteMarkedColumns(Map<String, Object> vedtak, Map<String, Object> endringsvedtak,
                                               Map<String, Object> nyRettighet) {

        endringsvedtak.forEach((column, value) -> {
            if (OVERWRITE_MARKER.equals(value)) {
                if (!nyRettighet.containsKey(column)) {
                    throw new IllegalStateException("Kolonnen %s finnes ikke i O-modellen".formatted(column));
                }
                vedtak.put(column, nyRettighet.get(column));
            }
        });

    }

    private static void applyKernelDensities(TilleggsstonadModel model, List<Map<String, Object>> historikk, RandomGenerator random) {

        for (var vedtak : historikk) {
            model.getKernelDensities().forEach((column, density) -> {
                if (!vedtak.containsKey(column)) {
                    throw new IllegalStateException("Kolonnen %s finnes ikke i vedtaket".formatted(column));
                }
                var verdi = PythonValues.toFloatOrNaN(vedtak.get(column)) * kernelDensityFactor(density.sample(random));
                vedtak.put(column, Double.isNaN(verdi) ? null : verdi);
            });
        }

    }

    private static double kernelDensityFactor(double sample) {

        var truncated = PythonValues.toInt(sample * 100) / 100.0;
        return truncated > 0 ? truncated : 0.0;

    }

    private static IllegalArgumentException unsupportedVedtaktype(String vedtaktype) {
        return new IllegalArgumentException("Vedtaktype %s er ikke støttet for denne stønadstypen".formatted(vedtaktype));
    }

    @RequiredArgsConstructor
    private static class Batches {

        private final TilleggsstonadModel model;
        private final RandomGenerator random;
        private final Map<String, Integer> rowIndexes = new HashMap<>();

        Map<String, Object> next(String vedtaktype, String utfall) {

            var synthesizer = model.synthesizer(vedtaktype, utfall);
            var rowIndex = rowIndexes.merge(vedtaktype + "/" + utfall, 1, Integer::sum) - 1;
            return new LinkedHashMap<>(synthesizer.synthesizeRow(rowIndex, random));

        }

    }

}
