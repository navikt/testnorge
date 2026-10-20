package no.nav.testnav.apps.statusfrontend.fagsystem.inntektsmelding;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendStatusClient;
import no.nav.testnav.apps.statusfrontend.fagsystem.technical.DollyBackendTechnicalStatus;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix = "functional-test.inntektsmelding", name = "technical-only", havingValue = "true")
public class InntektsmeldingTechnicalStatus extends DollyBackendTechnicalStatus {

    public InntektsmeldingTechnicalStatus(DollyBackendStatusClient client) {
        super(
                client,
                "inntektsmelding",
                "Inntektsmelding",
                "Inntektsmelding (ALTINN/JOARK)");
    }
}
