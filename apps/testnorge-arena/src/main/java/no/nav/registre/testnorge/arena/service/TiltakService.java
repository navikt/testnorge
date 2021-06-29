package no.nav.registre.testnorge.arena.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.TiltakArenaForvalterConsumer;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

import org.springframework.stereotype.Service;

import static no.nav.registre.testnorge.arena.service.util.RequestUtils.getRettighetFinnTiltakRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class TiltakService {

    private final TiltakArenaForvalterConsumer tiltakArenaForvalterConsumer;

    public NyttVedtakTiltak finnTiltak(String personident, String miljoe, NyttVedtakTiltak tiltaksdeltakelse) {
        var response = tiltakArenaForvalterConsumer.finnTiltak(getRettighetFinnTiltakRequest(personident, miljoe, tiltaksdeltakelse));
        if (response != null && !response.getNyeRettigheterTiltak().isEmpty()) {
            return response.getNyeRettigheterTiltak().get(0);
        } else {
            log.info("Fant ikke tiltak for tiltakdeltakelse.");
            return null;
        }
    }

}
