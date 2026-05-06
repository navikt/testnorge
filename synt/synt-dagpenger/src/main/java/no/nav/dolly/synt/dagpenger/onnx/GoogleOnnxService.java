package no.nav.dolly.synt.dagpenger.onnx;

import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Profile("prod")
class GoogleOnnxService implements OnnxService {

    @Value("${app.config.bucket:}")
    private final String bucket;

    private final DagpengevedtakGenerator dagpenger;

    GoogleOnnxService(String bucket) {
        if (!StringUtils.hasLength(bucket)) {
            throw new IllegalStateException("app.config.bucket must be configured");
        }
        this.bucket = bucket;
        // TODO: Download and prepare models, then create a dagpengerGeneratorService with those.
        this.dagpenger = new DagpengevedtakGenerator(null); // Placeholder, replace with actual model path
    }

    @Override
    public List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates) {

        var rettighetType = RettighetType.valueOf(rettighet.toUpperCase(Locale.ROOT));
        return dagpenger.generateVedtak(rettighetType, startDates).stream()
                .map(VedtakMapper::fromPrediction)
                .collect(Collectors.toList());

    }

}
