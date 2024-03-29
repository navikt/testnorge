package no.nav.testnav.apps.oversiktfrontend.controller;

import no.nav.testnav.apps.oversiktfrontend.domain.Application;
import no.nav.testnav.apps.oversiktfrontend.dto.ApplicationDTO;
import no.nav.testnav.apps.oversiktfrontend.service.ApplicationService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final CacheControl cacheControl;

    public ApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
        this.cacheControl = CacheControl.maxAge(10, TimeUnit.MINUTES).noTransform().mustRevalidate();
    }

    @GetMapping
    public ResponseEntity<Flux<ApplicationDTO>> getApplications() {
        var applications = applicationService.getApplications().map(Application::toDTO);
        return ResponseEntity
                .ok()
                .cacheControl(cacheControl)
                .body(applications);
    }

}
