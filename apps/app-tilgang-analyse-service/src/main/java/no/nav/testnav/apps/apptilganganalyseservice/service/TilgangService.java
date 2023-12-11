package no.nav.testnav.apps.apptilganganalyseservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import no.nav.testnav.apps.apptilganganalyseservice.domain.Access;

@Service
@RequiredArgsConstructor
public class TilgangService {
    private final ReadApplicationConfigService readApplicationConfigService;
    private final ReadDeployConfigService readDeployConfigService;

    public Mono<Access> fetchAccessBy(String appName, String owner, String repo) {
        return Mono.zip(
                readApplicationConfigService.getConfigBy(appName, owner, repo).collectList(),
                readDeployConfigService.getConfigBy(owner, repo).collectList()
        ).map(zipped -> new Access(appName, zipped.getT1(), zipped.getT2()));
    }
}
