package no.nav.testnav.apps.statusfrontend.fagsystem.aareg;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "functional-test.aareg", name = "technical-only", havingValue = "true")
public class AaregTechnicalStatus extends DollyBackendTechnicalStatus {

    public AaregTechnicalStatus(DollyBackendStatusClient client) {
        super(client, "aareg", "Arbeidsregisteret (AAREG)", "Arbeidsregister (AAREG)");
    }
}
