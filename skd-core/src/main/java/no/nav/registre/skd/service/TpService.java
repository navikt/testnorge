package no.nav.registre.skd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.skd.consumer.TpConsumer;

@Service
public class TpService {

    @Autowired
    private TpConsumer tpConsumer;

    public List<String> leggTilIdenterITp(List<String> identer, String miljoe) {
        ResponseEntity<List<String>> tpResponse = tpConsumer.leggTilIdenterITp(identer, miljoe);
        return tpResponse.getBody();
    }
}
