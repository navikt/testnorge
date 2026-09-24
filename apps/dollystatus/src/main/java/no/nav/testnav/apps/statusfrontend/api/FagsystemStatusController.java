package no.nav.testnav.apps.statusfrontend.api;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.api.model.FagsystemStatusResponse;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCoordinator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fagsystem-statuser")
@RequiredArgsConstructor
public class FagsystemStatusController {

    private final FunctionalTestCoordinator coordinator;

    @GetMapping
    public Mono<List<FagsystemStatusResponse>> getStatuses() {
        return coordinator.getSystemStatuses()
                .map(statuses -> statuses.stream()
                        .map(FagsystemStatusResponse::from)
                        .toList());
    }
}
