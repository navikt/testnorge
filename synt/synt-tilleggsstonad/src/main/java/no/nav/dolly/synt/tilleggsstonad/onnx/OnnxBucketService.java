package no.nav.dolly.synt.tilleggsstonad.onnx;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.SyntTilleggsstonadApplication;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import no.nav.dolly.synt.tilleggsstonad.models.BucketModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Profile("prod")
@Slf4j
@RequiredArgsConstructor
class OnnxBucketService implements OnnxService {

    private final SyntTilleggsstonadApplication.Config config;
    private TilleggsstonadModelGenerator generator;

    @PostConstruct
    void postConstruct()
            throws Exception {

        var modelDirectory = BucketModels.get(config.getBucket(), config.getModels(), "synt-tilleggsstonad-models-");
        this.generator = new TilleggsstonadModelGenerator(modelDirectory);
        log.info("Successfully initialized legacy model metadata from bucket {}", config.getBucket());

    }

    @Override
    public List<Map<String, Object>> generateVedtak(TilleggsstonadType tilleggsstonadType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return generator.generateVedtak(tilleggsstonadType, requests, brukInnsendtTilDato);
    }

}
