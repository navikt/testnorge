package no.nav.testnav.apps.statusfrontend.fagsystem.sykemelding;

import no.nav.testnav.apps.statusfrontend.fagsystem.technical.SecondBatchTechnicalStatusClient;
import no.nav.testnav.apps.statusfrontend.functionaltest.TechnicalStatusDefinition;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.DisplayName;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestContext;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.FunctionalTestEnvironment;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.TechnicalStatusDescriptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;

@Service
@ConditionalOnProperty(
        prefix = "functional-test.sykemelding",
        name = "technical-only",
        havingValue = "true")
public class SykemeldingTechnicalStatus implements TechnicalStatusDefinition {

    private static final TechnicalStatusDescriptor DESCRIPTOR = new TechnicalStatusDescriptor(
            new SystemId("sykemelding"),
            new DisplayName("Sykemelding"),
            Set.of(FunctionalTestEnvironment.GLOBAL));

    private final SecondBatchTechnicalStatusClient client;

    public SykemeldingTechnicalStatus(SecondBatchTechnicalStatusClient client) {
        this.client = client;
    }

    @Override
    public TechnicalStatusDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public Mono<Void> check(FunctionalTestContext context) {
        return client.checkDollyProxy();
    }
}
