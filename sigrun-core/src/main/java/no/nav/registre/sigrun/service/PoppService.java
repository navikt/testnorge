package no.nav.registre.sigrun.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import no.nav.registre.sigrun.consumer.rs.PoppSyntetisererenConsumer;
import no.nav.registre.sigrun.consumer.rs.SigrunStubConsumer;

@Service
public class PoppService {

    @Autowired
    private PoppSyntetisererenConsumer poppSyntRestConsumer;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    public ResponseEntity getPoppMeldinger(List<String> fnrs) {
        List<Map<String, Object>> result = poppSyntRestConsumer.getPoppMeldingerFromSyntRest(fnrs);
        return sigrunStubConsumer.sendDataToSigrunstub(result);
    }
}
