package no.nav.dolly.synt.tilleggsstonad.onnx;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import no.nav.dolly.synt.tilleggsstonad.models.LocalModels;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Profile("local")
@Slf4j
class OnnxLocalService implements OnnxService {

    private TilleggsstonadModelGenerator generator;

    @PostConstruct
    void postConstruct() {

        var modelDirectory = LocalModels.get();
        this.generator = new TilleggsstonadModelGenerator(modelDirectory);
        log.info("Successfully initialized legacy model metadata from folder {}", modelDirectory.toAbsolutePath());

    }

    @Override
    public List<Map<String, Object>> generateVedtak(TilleggsstonadType tilleggsstonadType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return generator.generateVedtak(tilleggsstonadType, requests, brukInnsendtTilDato);
    }

}
