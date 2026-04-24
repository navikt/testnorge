package no.nav.dolly.synt.dagpenger.onnx;

import java.util.Map;

public record OnnxPrediction(
        String modelFile,
        String predictedLabel,
        double confidence,
        Map<String, Double> probabilities
) {
}

