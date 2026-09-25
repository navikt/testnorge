package no.nav.testnav.apps.statusfrontend.fagsystem.tags;

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
@ConditionalOnProperty(prefix = "functional-test.tags", name = "enabled", havingValue = "true")
public class TagsTechnicalStatus implements TechnicalStatusDefinition {

    private static final TechnicalStatusDescriptor DESCRIPTOR = new TechnicalStatusDescriptor(
            new SystemId("tags"),
            new DisplayName("Tags/Hendelseslager"),
            Set.of(FunctionalTestEnvironment.GLOBAL));

    private final TagsClient client;

    public TagsTechnicalStatus(TagsClient client) {
        this.client = client;
    }

    @Override
    public TechnicalStatusDescriptor descriptor() {
        return DESCRIPTOR;
    }

    @Override
    public boolean requiresPdl() {
        return true;
    }

    @Override
    public Mono<Void> check(FunctionalTestContext context) {
        return client.checkTags(context.runId());
    }
}
