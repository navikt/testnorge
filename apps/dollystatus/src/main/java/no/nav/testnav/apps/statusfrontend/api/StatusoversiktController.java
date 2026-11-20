package no.nav.testnav.apps.statusfrontend.api;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.api.model.StatusoversiktResponse;
import no.nav.testnav.apps.statusfrontend.functionaltest.StatusoversiktService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/statusoversikt")
@RequiredArgsConstructor
public class StatusoversiktController {

    private final StatusoversiktService statusoversiktService;

    @GetMapping
    public Mono<StatusoversiktResponse> getStatusoversikt() {
        return statusoversiktService.getStatusoversikt();
    }
}
