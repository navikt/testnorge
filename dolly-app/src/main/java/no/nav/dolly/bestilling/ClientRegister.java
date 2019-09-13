package no.nav.dolly.bestilling;

import java.util.List;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.NorskIdent;
import no.nav.dolly.domain.resultset.RsDollyBestilling;

public interface ClientRegister {

    void gjenopprett(RsDollyBestilling bestilling, NorskIdent norskIdent, BestillingProgress progress);

    void release(List<String> identer);
}
