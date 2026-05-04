package no.nav.dolly.synt.dagpenger.onnx;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class DagpengerGeneratorBean {

    private final DagpengerInferenceService inferenceService;

    public DagpengerGeneratorBean(DagpengerInferenceService inferenceService) {
        this.inferenceService = inferenceService;
    }

    public List<MinimalDagpengevedtak> generateVedtak(RettighetType rettighet, List<String> vedtakStartDatoer) {
        return vedtakStartDatoer.stream()
                .map(LocalDate::parse)
                .map(date -> toVedtak(rettighet, date))
                .toList();
    }

    public Map<String, Object> getModelMetadata() {
        return Map.of(
                "modelDir", inferenceService.getModelDir().toString(),
                "selectedModels", inferenceService.getSelectedModels()
        );
    }

    private MinimalDagpengevedtak toVedtak(RettighetType rettighet, LocalDate date) {
        OnnxPrediction prediction = inferenceService.infer(rettighet, date);
        return new MinimalDagpengevedtak(
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
}

