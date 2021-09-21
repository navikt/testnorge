package no.nav.registre.skd.service;

import org.springframework.stereotype.Service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import no.nav.registre.skd.consumer.TpConsumer;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class TpService {

    private TpConsumer tpConsumer;

    public List<String> leggTilIdenterITp(List<String> identer, String miljoe) {
        return tpConsumer.leggTilIdenterITp(identer, miljoe);
    }
}
