package no.nav.dolly.synt.meldekort.onnx;

import java.util.Map;

record OnnxPrediction(
        String modelFile,
        String predictedLabel,
        double confidence,
        Map<String, Double> probabilities
) {
}

