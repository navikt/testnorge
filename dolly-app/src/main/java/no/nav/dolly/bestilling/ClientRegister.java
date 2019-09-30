package no.nav.dolly.bestilling;

import java.util.List;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

public interface ClientRegister {

    void gjenopprett(RsDollyBestilling bestilling, TpsPerson tpsPerson, BestillingProgress progress);

    void release(List<String> identer);
}
