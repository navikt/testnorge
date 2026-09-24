package no.nav.testnav.apps.statusfrontend.fagsystem.tpsmessaging;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        prefix = "functional-test.tps-messaging-bankkonto",
        name = "technical-only",
        havingValue = "true")
public class TpsBankkontoTechnicalStatus extends DollyBackendTechnicalStatus {

    public TpsBankkontoTechnicalStatus(DollyBackendStatusClient client) {
        super(client, "tps-messaging-bankkonto", "TPS Messaging bankkonto", "TpsMessaging");
    }
}
