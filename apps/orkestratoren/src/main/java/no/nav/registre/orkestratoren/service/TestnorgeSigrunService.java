package no.nav.registre.orkestratoren.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.TestnorgeSigrunConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestnorgeSigrunService {

    private final TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    public ResponseEntity<List<Integer>> genererSkattegrunnlag(SyntetiserPoppRequest syntetiserPoppRequest, String testdataEier) {
        var response = testnorgeSigrunConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier);
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
