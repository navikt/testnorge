package no.nav.dolly.bestilling.arena;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;

@Slf4j
@Service
public class ArenaStubClient implements ClientRegister {

    @Autowired
    private ArenaStubConsumer arenaStubConsumer;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        if (nonNull(bestilling.getArenastub())) {
            try {
                arenaStubConsumer.deleteIdent(norskIdent.getIdent());
                bestilling.getArenastub().setPersonident(norskIdent.getIdent());
                ResponseEntity response = arenaStubConsumer.postArenadata(bestilling.getArenastub());
                progress.setArenastubStatus(response.getStatusCode().name());

            } catch (RuntimeException e) {
                progress.setArenastubStatus(format("Feil: %s", e.getMessage()));
                log.error("Feilet å oppdatere ArenaStub: ", e);
            }
        }
    }
}
