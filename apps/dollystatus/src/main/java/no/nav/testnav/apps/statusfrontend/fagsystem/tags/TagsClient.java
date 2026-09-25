package no.nav.testnav.apps.statusfrontend.fagsystem.tags;

import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import reactor.core.publisher.Mono;

public interface TagsClient {

    Mono<Void> checkTags(RunId runId);
}
