package no.nav.dolly.synt.meldekort.onnx;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@UtilityClass
class MeldekortMapper {

    GeneratedMeldekort fromPredictions(
            MeldekortType meldekortType,
            int predictionIndex,
            List<OnnxPrediction> predictions,
            Double arbeidstimerOverride) {

        var signalA = averageProbability(predictions, "class_0");
        var signalB = averageProbability(predictions, "class_1");
        var signalC = averageProbability(predictions, "class_2");
        var seed = Objects.hash(
                meldekortType.name(),
                predictionIndex,
                predictions.stream().map(OnnxPrediction::modelFile).toList(),
                predictions.stream().map(OnnxPrediction::predictedLabel).toList(),
                predictions.stream().map(OnnxPrediction::confidence).toList()
        );
        var rng = new Random(seed);

        var arbeidssoker = rng.nextDouble() < clamp(0.15 + (signalA * 0.7), 1.0);
        var arbeidet = rng.nextDouble() < clamp(0.20 + (signalB * 0.65), 1.0);
        var syk = rng.nextDouble() < clamp(0.05 + (signalC * 0.45), 1.0);
        var annetFravaer = rng.nextDouble() < clamp(0.03 + ((1.0 - signalA) * 0.30), 1.0);
        var kurs = rng.nextDouble() < clamp((meldekortType == MeldekortType.ATTF ? 0.25 : 0.05) + (signalB * 0.35), 1.0);
        var forskudd = rng.nextDouble() < clamp(0.10 + ((1.0 - signalB) * 0.25) + (signalC * 0.20), 1.0);

        var dagmeldinger = new ArrayList<GeneratedMeldekortDag>();
        var timerBase = resolveBaseArbeidstimer(arbeidstimerOverride, signalA, signalB, signalC, rng);
        for (int dag = 1; dag <= 14; dag++) {
            var dayJitter = (rng.nextDouble() - 0.5) * 2.0;
            var isSyk = rng.nextDouble() < clamp(0.03 + (signalC * 0.35), 1.0);
            var isAnnetFravaer = rng.nextDouble() < clamp(0.02 + ((1.0 - signalA) * 0.18), 1.0);
            var isKurs = rng.nextDouble() < clamp((meldekortType == MeldekortType.ATTF ? 0.18 : 0.03) + (signalB * 0.12), 1.0);
            var timer = arbeidstimerOverride != null
                    ? roundToOneDecimal(arbeidstimerOverride)
                    : roundToOneDecimal(clamp(timerBase + dayJitter + (isSyk ? -0.5 : 0.3), 37.5));
            dagmeldinger.add(new GeneratedMeldekortDag(
                    dag,
                    timer,
                    isSyk,
                    isAnnetFravaer,
                    isKurs,
                    meldekortType.name()
            ));
        }

        return new GeneratedMeldekort(
                arbeidssoker,
                arbeidet,
                syk,
                annetFravaer,
                kurs,
                forskudd,
                "true",
                dagmeldinger
        );

    }

    private static double resolveBaseArbeidstimer(Double arbeidstimerOverride, double signalA, double signalB, double signalC, Random rng) {
        if (arbeidstimerOverride != null) {
            return roundToOneDecimal(arbeidstimerOverride);
        }
        var generated = 1.5 + (signalA * 2.5) + (signalB * 3.5) + ((1.0 - signalC) * 1.5) + (rng.nextDouble() * 1.5);
        return roundToOneDecimal(generated);
    }

    private static double averageProbability(List<OnnxPrediction> predictions, String key) {
        if (predictions.isEmpty()) {
            return 0.5;
        }
        return predictions
                .stream()
                .map(OnnxPrediction::probabilities)
                .mapToDouble(probabilities -> probabilities.getOrDefault(key, 0.5))
                .average()
                .orElse(0.5);
    }

    private static double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private static double clamp(double value, double max) {
        return Math.clamp(value, 0.0, max);
    }

}

