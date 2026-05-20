package no.nav.dolly.synt.dagpenger.onnx;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.dagpenger.SyntDagpengerApplication;
import no.nav.dolly.synt.dagpenger.bucket.BucketUtils;
import no.nav.dolly.synt.dagpenger.dto.DagpengevedtakDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
class OnnxBucketService implements OnnxService {

    private final SyntDagpengerApplication.Config config;
    private DagpengevedtakGenerator dagpenger;

    @PostConstruct
    void initialize()
            throws Exception {

        var modelDirectory = BucketUtils.download(config.getBucket(), config.getModels(), "synt-dagpenger-models-");
        this.dagpenger = new DagpengevedtakGenerator(modelDirectory);
        log.info("Successfully initialized ONNX models from GCS bucket: {}", config.getBucket());

    }

    @Override
    public List<DagpengevedtakDto> generateVedtak(String rettighet, List<String> startDates) {

        var rettighetType = RettighetType.valueOf(rettighet.toUpperCase(Locale.ROOT));
        return dagpenger
                .generateVedtak(rettighetType, startDates)
                .stream()
                .map(VedtakMapper::fromPrediction)
                .collect(Collectors.toList());

    }

}
