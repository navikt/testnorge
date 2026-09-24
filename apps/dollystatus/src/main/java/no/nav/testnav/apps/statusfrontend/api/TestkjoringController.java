package no.nav.testnav.apps.statusfrontend.api;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.api.model.RunAcceptedResponse;
import no.nav.testnav.apps.statusfrontend.api.model.TestRunResponse;
import no.nav.testnav.apps.statusfrontend.functionaltest.FunctionalTestCoordinator;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.RunId;
import no.nav.testnav.apps.statusfrontend.functionaltest.model.SystemId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TestkjoringController {

    private final FunctionalTestCoordinator coordinator;

    @PostMapping("/testkjoringer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<RunAcceptedResponse> startExpiredTests() {
        return coordinator.startAllExpired()
                .map(RunAcceptedResponse::from);
    }

    @GetMapping("/testkjoringer/{runId}")
    public Mono<TestRunResponse> getRun(@PathVariable String runId) {
        return coordinator.getRun(RunId.from(runId))
                .map(TestRunResponse::from);
    }

    @PostMapping("/fagsystemer/{systemId}/testkjoringer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<RunAcceptedResponse> startSystemTest(@PathVariable String systemId) {
        return coordinator.startSystem(SystemId.from(systemId))
                .map(RunAcceptedResponse::from);
    }
}
