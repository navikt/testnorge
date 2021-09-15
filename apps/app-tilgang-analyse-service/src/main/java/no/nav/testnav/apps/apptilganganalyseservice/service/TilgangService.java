package no.nav.testnav.apps.apptilganganalyseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.apptilganganalyseservice.domain.Access;

@Service
@RequiredArgsConstructor
public class TilgangService {
    private final ApplicationService applicationService;

    public Mono<Access> fetchAccessBy(String appName, String repo) {
        return applicationService
                .fetchAppsBy(appName, repo)
                .collectList()
                .map(list -> new Access(appName, list));
    }
}
