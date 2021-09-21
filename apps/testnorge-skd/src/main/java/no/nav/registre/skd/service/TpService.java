package no.nav.registre.skd.service;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import no.nav.registre.skd.consumer.TpConsumer;

@Service
@RequiredArgsConstructor
public class TpService {

    private final TpConsumer tpConsumer;

    public List<String> leggTilIdenterITp(List<String> identer, String miljoe) {
        return tpConsumer.leggTilIdenterITp(identer, miljoe);
    }
}
