package no.nav.testnav.apps.statusfrontend.fagsystem.pensjon;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "functional-test.pensjon-oevrige", name = "technical-only", havingValue = "true")
public class PensjonOevrigeTechnicalStatus extends DollyBackendTechnicalStatus {

    public PensjonOevrigeTechnicalStatus(DollyBackendStatusClient client) {
        super(client, "pensjon-oevrige", "Pensjon, øvrige profiler", "Pensjon");
    }
}
