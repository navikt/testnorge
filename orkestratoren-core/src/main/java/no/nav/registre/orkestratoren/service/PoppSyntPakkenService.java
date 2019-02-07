package no.nav.registre.orkestratoren.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.consumer.rs.PoppSyntConsumer;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@Service
@Slf4j
public class PoppSyntPakkenService {

    @Autowired
    private PoppSyntConsumer poppSyntConsumer;

    public ResponseEntity genererSkattegrunnlag(SyntetiserPoppRequest syntetiserPoppRequest, String testdataEier) {
        ResponseEntity response = poppSyntConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier);
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
