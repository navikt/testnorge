package no.nav.dolly.bestilling.pdlforvalter;

import static java.util.Objects.nonNull;

import org.springframework.stereotype.Service;

import no.nav.dolly.domain.resultset.RsDollyBestillingRequest;

/**
 * Hensikt med denne tjeneste å bestemme om bestillingen krever synkronisering med opprettelse i PDL.
 * Integrerer Dolly med spesifikke systemer i bestillingen som gjør oppslag til aktørregister?
 * Disse identifiseres her.
 */
@Service
public class PdlSyncDeterminator {

    public boolean isRequiredSync(RsDollyBestillingRequest request) {

        return nonNull(request.getAareg());
    }
}