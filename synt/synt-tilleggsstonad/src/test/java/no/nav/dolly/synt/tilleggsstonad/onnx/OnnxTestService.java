package no.nav.dolly.synt.tilleggsstonad.onnx;

import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Profile("test")
class OnnxTestService implements OnnxService {

    @Override
    public List<Map<String, Object>> generateVedtak(TilleggsstonadType tilleggsstonadType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato) {
        return List.of();
    }

}
