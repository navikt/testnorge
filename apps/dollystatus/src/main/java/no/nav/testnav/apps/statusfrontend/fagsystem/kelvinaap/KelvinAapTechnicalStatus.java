package no.nav.testnav.apps.statusfrontend.fagsystem.kelvinaap;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "functional-test.kelvin-aap", name = "technical-only", havingValue = "true")
public class KelvinAapTechnicalStatus extends DollyBackendTechnicalStatus {

    public KelvinAapTechnicalStatus(DollyBackendStatusClient client) {
        super(client, "kelvin-aap", "Kelvin AAP", "KelvinAap");
    }
}
