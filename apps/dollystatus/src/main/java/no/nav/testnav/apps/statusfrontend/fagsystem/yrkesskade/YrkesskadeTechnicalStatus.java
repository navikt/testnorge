package no.nav.testnav.apps.statusfrontend.fagsystem.yrkesskade;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "functional-test.yrkesskade", name = "technical-only", havingValue = "true")
public class YrkesskadeTechnicalStatus extends DollyBackendTechnicalStatus {

    public YrkesskadeTechnicalStatus(DollyBackendStatusClient client) {
        super(client, "yrkesskade", "Yrkesskade", "Yrkesskade");
    }
}
