package no.nav.dolly.bestilling;

import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.domain.resultset.RsOrganisasjonBestilling;

import java.util.List;

public interface OrganisasjonRegister {

    void opprett(RsOrganisasjonBestilling bestilling, Long bestillingId);

    DeployResponse gjenopprett(DeployRequest request);

    void release(List<String> organisasjoner);
}