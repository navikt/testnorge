package no.nav.dolly.synt.dagpenger.onnx;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

public interface DagpengerInferenceService {

    OnnxPrediction infer(RettighetType rettighet, LocalDate vedtakDate);

    Path getModelDir();

    Map<RettighetType, String> getSelectedModels();
}

