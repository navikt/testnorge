package no.nav.testnav.apps.statusfrontend.fagsystem.dokarkiv;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "functional-test.dokarkiv", name = "technical-only", havingValue = "true")
public class DokarkivTechnicalStatus extends DollyBackendTechnicalStatus {

    public DokarkivTechnicalStatus(DollyBackendStatusClient client) {
        super(client, "dokarkiv", "Dokumentarkiv (JOARK)", "Dokumentarkiv (JOARK)");
    }
}
