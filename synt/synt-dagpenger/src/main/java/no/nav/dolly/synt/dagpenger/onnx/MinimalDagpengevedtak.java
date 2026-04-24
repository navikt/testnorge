package no.nav.dolly.synt.dagpenger.onnx;

import java.util.Map;

public record MinimalDagpengevedtak(
        String rettighetKode,
        String vedtaktypeKode,
        String datoMottatt,
        String beregningsdato,
        String modellFil,
        String predikertLabel,
        double confidence,
        Map<String, Double> sannsynligheter
) {
}
