package no.nav.registre.sigrun.service;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SigrunService {

    @Autowired
    private PoppSyntetisererenConsumer poppSyntRestConsumer;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private SigrunStubConsumer sigrunStubConsumer;

    public List<String> finnEksisterendeOgNyeIdenter(SyntetiserPoppRequest syntetiserPoppRequest) {
        List<String> eksisterendeIdenter = finnEksisterendeIdenter(syntetiserPoppRequest.getMiljoe());
        List<String> nyeIdenter = finnLevendeIdenter(syntetiserPoppRequest);

        int antallIdenterAlleredeIStub = 0;

        for (String ident : nyeIdenter) {
            if (eksisterendeIdenter.contains(ident)) {
                antallIdenterAlleredeIStub++;
            } else {
                eksisterendeIdenter.add(ident);
            }
        }

        if (antallIdenterAlleredeIStub > 0) {
            log.info("{} av de nyutvalgte identene eksisterte allerede i sigrun-skd-stub.", antallIdenterAlleredeIStub);
        }

        return eksisterendeIdenter;
    }

    public ResponseEntity genererPoppmeldingerOgSendTilSigrunStub(List<String> identer, String testdataEier, String miljoe) {
        List<Map<String, Object>> syntetiserteMeldinger = finnSyntetiserteMeldinger(identer);
        return sigrunStubConsumer.sendDataTilSigrunstub(syntetiserteMeldinger, testdataEier, miljoe);
    }

    private List<Map<String, Object>> finnSyntetiserteMeldinger(List<String> fnrs) {
        return poppSyntRestConsumer.hentPoppMeldingerFromSyntRest(fnrs);
    }

    private List<String> finnEksisterendeIdenter(String miljoe) {
        return sigrunStubConsumer.hentEksisterendePersonidentifikatorer(miljoe);
    }

    private List<String> finnLevendeIdenter(SyntetiserPoppRequest syntetiserPoppRequest) {
        return hodejegerenConsumer.finnLevendeIdenter(syntetiserPoppRequest);
    }
}
