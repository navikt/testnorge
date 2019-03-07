package no.nav.registre.sam.service;

import no.nav.registre.sam.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sam.consumer.rs.SamSyntetisererenConsumer;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SyntetiseringService {

    @Autowired
    SamSyntetisererenConsumer samSyntRestConsumer;

    @Autowired
    HodejegerenConsumer hodejegerenConsumer;

    public ResponseEntity finnSyntetiserteMeldinger(List<String> identer) {
        List<Map<String, String>> syntetiserteMeldinger = samSyntRestConsumer.hentSammeldingerFromSyntRest(identer.size());
        return ResponseEntity.ok().body(syntetiserteMeldinger);
    }

    public List<String> finnLevendeIdenter(SyntetiserSamRequest request) {
        return hodejegerenConsumer.finnLevendeIdenter(request);
    }
}