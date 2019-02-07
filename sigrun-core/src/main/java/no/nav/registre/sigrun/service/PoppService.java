package no.nav.registre.sigrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.sigrun.consumer.rs.HodejegerenConsumer;
import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;
import no.nav.registre.sigrun.provider.rs.requests.SyntetiserPoppRequest;

@Service
public class PoppService {

    @Autowired
    private PoppSyntetisererenConsumer poppSyntRestConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    public List<String> finnLevendeIdenter(SyntetiserPoppRequest syntetiserPoppRequest) {
        return hodejegerenConsumer.finnLevendeIdenter(syntetiserPoppRequest);
    }

    public ResponseEntity getPoppMeldinger(List<String> fnrs, String testdataEier) {
        List<Map<String, Object>> result = poppSyntRestConsumer.getPoppMeldingerFromSyntRest(fnrs);
        return sigrunStubConsumer.sendDataToSigrunstub(result, testdataEier);
    }
}
