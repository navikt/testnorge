package no.nav.registre.inst.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.inst.consumer.rs.InstSyntetisererenConsumer;
import no.nav.registre.inst.provider.rs.requests.SyntetiserInstRequest;

@Service
@Slf4j
public class SyntetiseringService {

    @Autowired
    InstSyntetisererenConsumer instSyntRestConsumer;

    public ResponseEntity finnSyntetiserteMeldinger(SyntetiserInstRequest syntetiserInstRequest) {
        List<Map<String, String>> syntetiserteMeldinger = instSyntRestConsumer.hentInstMeldingerFromSyntRest(syntetiserInstRequest.getAntallNyeIdenter());
        return ResponseEntity.ok().body(syntetiserteMeldinger);
    }
}
