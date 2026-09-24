package no.nav.testnav.apps.statusfrontend.fagsystem.brregstub;

import reactor.core.publisher.Mono;

public interface BrregstubClient {

    Mono<BrregstubResourceStatus> getRoleOverview(BrregstubRequest expectedRequest);

    Mono<BrregstubResourceStatus> getOrganization(BrregstubRequest expectedRequest);

    Mono<Void> createRoleOverview(BrregstubRequest request);

    Mono<Void> deleteRoleOverview(String ident);

    Mono<Void> deleteOrganization();
}
