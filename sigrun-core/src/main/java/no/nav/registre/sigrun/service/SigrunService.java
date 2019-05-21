package no.nav.registre.sigrun.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sigrun.IdentMedData;
import no.nav.registre.sigrun.SigrunSaveInHodejegerenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private static final String SIGRUN_NAME = "sigrun";

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
        ResponseEntity response =  sigrunStubConsumer.sendDataTilSigrunstub(syntetiserteMeldinger, testdataEier, miljoe);
        if (response.getStatusCode().is2xxSuccessful()){
            List<IdentMedData> identerMedData = new ArrayList<>(identer.size());
            if (identer.size() == syntetiserteMeldinger.size()){
                for (int i = 0; i < identer.size(); i++){
                    identerMedData.add(new IdentMedData(identer.get(i), Collections.singletonList(syntetiserteMeldinger.get(i))));
                }
            }
            SigrunSaveInHodejegerenRequest hodejegerenRequest = new SigrunSaveInHodejegerenRequest(SIGRUN_NAME, identerMedData);

            Set<String> savedIds = hodejegerenConsumer.saveHistory(hodejegerenRequest);
            if (savedIds.isEmpty()) {
                log.warn("Kunne ikke lagre historikk på noen identer");
            }
        }
        return response;
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
