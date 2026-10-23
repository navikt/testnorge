package no.nav.dolly.synt.tilleggsstonad.generator;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.synt.tilleggsstonad.dto.VedtakRequestDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class TilleggsstonadService {

    private final TilleggsstonadGenerator generator;

    public List<Map<String, Object>> generateVedtak(TilleggsstonadType type, List<VedtakRequestDto> requests,
                                                    boolean brukInnsendtTilDato) {
        return generator.generateVedtak(type, requests, brukInnsendtTilDato, ThreadLocalRandom.current());
    }

}
