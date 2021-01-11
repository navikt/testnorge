package no.nav.dolly.bestilling;

import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;

import java.util.List;

public interface OrganisasjonRegister {

    void opprett(RsOrganisasjonBestilling bestilling, Long bestillingId);

    void gjenopprett(OrganisasjonBestillingProgress progress, List<String> miljoer);

    void release(List<String> organisasjoner);
}