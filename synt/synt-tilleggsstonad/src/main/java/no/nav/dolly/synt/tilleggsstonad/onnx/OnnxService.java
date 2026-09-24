package no.nav.dolly.synt.tilleggsstonad.onnx;

import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;

import java.util.List;
import java.util.Map;

public interface OnnxService {

    List<Map<String, Object>> generateVedtak(TilleggsstonadType tilleggsstonadType, List<VedtakRequestDto> requests, boolean brukInnsendtTilDato);

}
