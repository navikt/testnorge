package no.nav.dolly.bestilling.pdlforvalter;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.pdlforvalter.folkeregister.PdlFolkeregisterIdent;

@Service
public class PdlConsumerClient implements ClientRegister {

    private static final String KILDE = "Dolly";

    @Autowired
    private PdlForvalterClient pdlForvalterClient;

    @Override public void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress) {

        pdlForvalterClient.postFolkeregisterIdent(PdlFolkeregisterIdent.builder()
                .idnummer(norskIdent.getIdent())
                .gyldigFom(LocalDate.now())
                .type(norskIdent.getIdentType())
                .kilde(KILDE)
                .build());
    }
}
