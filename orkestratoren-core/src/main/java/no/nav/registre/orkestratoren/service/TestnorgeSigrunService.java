package no.nav.registre.orkestratoren.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@Service
@Slf4j
public class TestnorgeSigrunService {

    @Autowired
    private TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    public ResponseEntity genererSkattegrunnlag(SyntetiserPoppRequest syntetiserPoppRequest, String testdataEier) {
        ResponseEntity response = testnorgeSigrunConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier);
        if (!response.getStatusCode().equals(HttpStatus.OK)) {
            log.warn("Noe feilet under syntetisering av skattegrunnlag. Vennligst se loggene til Testnorge-Sigrun for mer informasjon");
        } else {
            List<Integer> statuskoderFraSigrunSkdStub = (List<Integer>) response.getBody();
            if (statuskoderFraSigrunSkdStub != null) {
                log.info("Status fra skattegrunnlag sendt til sigrun-skd-stub: {}", statuskoderFraSigrunSkdStub);
            }
        }
        return response;
    }
}
