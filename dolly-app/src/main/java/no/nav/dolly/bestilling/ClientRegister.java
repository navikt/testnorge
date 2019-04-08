package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.resultset.RsDollyBestilling;

@FunctionalInterface
public interface ClientRegister {

    void gjenopprett(RsDollyBestilling bestilling, String ident, BestillingProgress progress);
}
